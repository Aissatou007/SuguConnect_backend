package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Entity.PanierProduit;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.PanierProduitRepository;
import odk.SuguConnect.Repository.PanierRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PanierService {
    private final ConsommateurRepository consommateurRepository;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    private final PanierProduitRepository panierProduitRepository;

    @Transactional
    public String ajouterProduitAuPanier(int consommateurId, int produitId, int quantite) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        Produit produit = findProduitById(produitId);
        Panier panier = obtenirOuCreerPanier(consommateur);

        verifierStockDisponible(produit, quantite);

        ajouterOuMettreAJourPanierProduit(panier, produit, quantite);

        if (!panier.getProduits().contains(produit)) {
            panier.getProduits().add(produit);
        }
        
        panierRepository.save(panier);
        return String.format("Produit %s ajouté au panier avec succès. Quantité: %d", produit.getNom(), quantite);
    }

    @Transactional
    public String retirerProduitDuPanier(int consommateurId, int produitId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        Panier panier = validerPanierNonVide(consommateur);

        PanierProduit panierProduit = trouverPanierProduit(panier, produitId);
        panier.getPanierProduits().remove(panierProduit);
        panierProduitRepository.delete(panierProduit);

        panier.getProduits().removeIf(p -> p.getId() == produitId);
        
        panierRepository.save(panier);
        return "Produit retiré du panier avec succès.";
    }
    
    public Panier voirPanier(int consommateurId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        Panier panier = consommateur.getPanier();

        if (panier == null) {

            panier = new Panier();
            panier.setConsommateur(consommateur);
            panier = panierRepository.save(panier);
            consommateur.setPanier(panier);
        }
        
        return panier;
    }

    
    private Consommateur findConsommateurById(int id) {
        return consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));
    }
    
    private Produit findProduitById(int id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
    }
    
    private Panier obtenirOuCreerPanier(Consommateur consommateur) {
        Panier panier = consommateur.getPanier();
        if (panier == null) {
            panier = new Panier();
            panier.setConsommateur(consommateur);
            consommateur.setPanier(panier);
            panier = panierRepository.save(panier);
        }
        return panier;
    }
    

    private Panier validerPanierNonVide(Consommateur consommateur) {
        Panier panier = consommateur.getPanier();
        if (panier == null || panier.getProduits().isEmpty()) {
            throw new EntityNotFoundException("Le panier est vide");
        }
        return panier;
    }
    
    private void verifierStockDisponible(Produit produit, int quantite) {
        if (produit.getStockDisponible() < quantite) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant pour %s. Stock disponible: %d",
                    produit.getNom(), produit.getStockDisponible())
            );
        }
    }
    
    private void ajouterOuMettreAJourPanierProduit(Panier panier, Produit produit, int quantite) {
        Optional<PanierProduit> existant = trouverPanierProduitOptional(panier, produit.getId());
        
        if (existant.isPresent()) {
            // Mettre à jour la quantité existante
            PanierProduit panierProduit = existant.get();
            int nouvelleQuantite = panierProduit.getQuantite() + quantite;
            verifierStockDisponible(produit, nouvelleQuantite);
            panierProduit.setQuantite(nouvelleQuantite);
            panierProduitRepository.save(panierProduit);
        } else {
            // Créer un nouveau PanierProduit
            PanierProduit nouveauPanierProduit = creerPanierProduit(panier, produit, quantite);
            panierProduitRepository.save(nouveauPanierProduit);
            panier.getPanierProduits().add(nouveauPanierProduit);
        }
    }
    
    private PanierProduit creerPanierProduit(Panier panier, Produit produit, int quantite) {
        PanierProduit panierProduit = new PanierProduit();
        panierProduit.setPanier(panier);
        panierProduit.setProduit(produit);
        panierProduit.setQuantite(quantite);
        panierProduit.setPrixUnitaire(produit.getPrixUnitaire());
        panierProduit.setDejaCommande(false);
        return panierProduit;
    }
    
    private Optional<PanierProduit> trouverPanierProduitOptional(Panier panier, int produitId) {
        return panier.getPanierProduits().stream()
                .filter(pp -> pp.getProduit().getId() == produitId)
                .findFirst();
    }
    
    private PanierProduit trouverPanierProduit(Panier panier, int produitId) {
        return trouverPanierProduitOptional(panier, produitId)
                .orElseThrow(() -> new EntityNotFoundException("Ce produit n'est pas dans votre panier"));
    }
}
