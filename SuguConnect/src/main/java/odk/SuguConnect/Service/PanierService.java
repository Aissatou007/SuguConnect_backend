package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.PanierRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PanierService {
    private final ConsommateurRepository consommateurRepository;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;

    public PanierService(ConsommateurRepository consommateurRepository,
                         PanierRepository panierRepository,
                         ProduitRepository produitRepository) {
        this.consommateurRepository = consommateurRepository;
        this.panierRepository = panierRepository;
        this.produitRepository = produitRepository;
    }

    @Transactional
    public String ajouterProduitAuPanier(int consommateurId, int produitId, int quantite) {
        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));

        if (produit.getStockDisponible() < quantite) {
            throw new IllegalArgumentException("Stock insuffisant pour ce produit");
        }

        Panier panier = consommateur.getPanier();
        if (panier == null) {
            panier = new Panier();
            panier.setConsommateur(consommateur);
            consommateur.setPanier(panier);
        }

        panier.getProduits().add(produit);
        produit.setStockDisponible(produit.getStockDisponible() - quantite);

        panierRepository.save(panier);
        return "Produit " + produit.getNom() + " ajouté au panier avec succès.";
    }

    @Transactional
    public String retirerProduitDuPanier(int consommateurId, int produitId) {
        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));

        Panier panier = consommateur.getPanier();
        if (panier == null || panier.getProduits().isEmpty()) {
            throw new IllegalArgumentException("Votre panier est vide");
        }

        boolean removed = panier.getProduits().removeIf(p -> p.getId() == produitId);
        if (!removed) {
            throw new EntityNotFoundException("Ce produit n'est pas dans votre panier");
        }

        panierRepository.save(panier);
        return "Produit retiré du panier avec succès.";
    }

    public Panier voirPanier(int consommateurId) {
        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));

        Panier panier = consommateur.getPanier();
        if (panier == null || panier.getProduits().isEmpty()) {
            throw new EntityNotFoundException("Le panier est vide");
        }

        return panier;
    }
}
