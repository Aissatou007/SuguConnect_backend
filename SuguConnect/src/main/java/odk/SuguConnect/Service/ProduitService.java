package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProduitService {
    private final ProduitRepository produitRepository;
    private final ProducteurRepository producteurRepository;
    private final CategorieRepository categorieRepository;
    
    private static final Pattern NOM_PRODUIT_PATTERN = Pattern.compile("^[a-zA-Z0-9À-ÿ\\s.'-]{3,50}$");
    
    // ========== Méthodes privées de validation ==========
    
    private void validerNomProduit(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        if (!NOM_PRODUIT_PATTERN.matcher(nom.trim()).matches()) {
            throw new IllegalArgumentException("Le nom du produit doit contenir entre 3 et 50 caractères alphanumériques");
        }
    }
    
    private void validerPrix(float prix) {
        if (prix <= 0) {
            throw new IllegalArgumentException("Le prix doit être supérieur à zéro");
        }
    }
    
    private void validerQuantite(int quantite) {
        if (quantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
    }
    
    // ========== Méthodes producteur ==========
    
    public Produit ajouterProduit(Produit produit, int producteurId) {
        // Validation des données
        validerNomProduit(produit.getNom());
        validerPrix(produit.getPrixUnitaire());
        validerQuantite(produit.getQuantite());
        
        if (produit.getPhotos() == null || produit.getPhotos().isEmpty()) {
            throw new IllegalArgumentException("Le produit doit contenir au moins une photo");
        }
        if(produit.getPhotos().size() > 4){
            throw new IllegalArgumentException("Le produit ne peut pas avoir plus de 4 photos");
        }
        
        // Vérifier et associer la catégorie si fournie
        if(produit.getCategorie() != null && produit.getCategorie().getId() > 0) {
            Categorie categorie = categorieRepository.findById(produit.getCategorie().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Cette catégorie n'existe pas"));
            produit.setCategorie(categorie);
        }
        
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Ce producteur n'existe pas"));
        produit.setProducteur(producteur);
        produit.setStockDisponible(produit.getQuantite());
        produitRepository.save(produit);
        return produit;
    }
    
    public Produit modifierProduit(Produit produitModifie , int produitId , int producteurId){
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Ce produit n'existe pas"));
        if(produit.getProducteur().getId() != producteurId){
            throw new SecurityException("Vous ne pouvez pas modifier ce produit");
        }
        
        // Valider le nom du produit s'il est fourni
        if(produitModifie.getNom() != null) {
            validerNomProduit(produitModifie.getNom());
            produit.setNom(produitModifie.getNom());
        }
        
        // Valider et mettre à jour le prix s'il est fourni
        if(produitModifie.getPrixUnitaire() > 0) {
            validerPrix(produitModifie.getPrixUnitaire());
            produit.setPrixUnitaire(produitModifie.getPrixUnitaire());
        }
        
        // Valider et mettre à jour la quantité si elle est fournie
        if(produitModifie.getQuantite() >= 0) {
            validerQuantite(produitModifie.getQuantite());
            produit.setQuantite(produitModifie.getQuantite());
            // Mettre à jour le stock disponible en conséquence
            produit.setStockDisponible(produitModifie.getQuantite());
        }
        
        // Mettre à jour la description si elle est fournie
        if(produitModifie.getDescription() != null) {
            produit.setDescription(produitModifie.getDescription());
        }
        
        // Mettre à jour l'unité si elle est fournie
        if(produitModifie.getUnite() != null) {
            produit.setUnite(produitModifie.getUnite());
        }
        
        // Mettre à jour la catégorie si elle est fournie
        if(produitModifie.getCategorie() != null && produitModifie.getCategorie().getId() > 0) {
            Categorie categorie = categorieRepository.findById(produitModifie.getCategorie().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Cette catégorie n'existe pas"));
            produit.setCategorie(categorie);
        }
        
        // Mettre à jour les photos si elles sont fournies
        if(produitModifie.getPhotos() != null && !produitModifie.getPhotos().isEmpty()) {
            if(produitModifie.getPhotos().size() > 4) {
                throw new IllegalArgumentException("Le produit ne peut pas avoir plus de 4 photos");
            }
            produit.setPhotos(produitModifie.getPhotos());
        }
        
        produitRepository.save(produit);
        return produit;
    }
    
    // ========== Méthodes publiques ==========
    
    public List<Produit> listerLesProduits(int producteurId){
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(()->new EntityNotFoundException("Ce producteur n'existe pas"));
        return produitRepository.findByProducteur(producteur);
    }
    
    public List<Produit> filtrerProduitsParNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }
    
    public List<Produit> filtrerProduitsParNomEtStockDisponible(String nom) {
        List<Produit> produits = produitRepository.findByNomContainingIgnoreCase(nom);
        return produits.stream()
                .filter(produit -> produit.getStockDisponible() > 0)
                .toList();
    }
    
    public String supprimerProduit(int produitId , int producteurId){
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(()->new EntityNotFoundException("Ce produit n'existe pas"));
        if(produit.getProducteur().getId() != producteurId){
            throw new IllegalArgumentException("Vous ne pouvez pas modifier ce produit");
        } produitRepository.delete(produit);
        return "Le produit a été supprimé";
    }
    
    /**
     * Récupère un produit par son ID
     * @param produitId ID du produit
     * @return Le produit correspondant
     * @throws EntityNotFoundException si le produit n'existe pas
     */
    public Produit getProduitById(int produitId) {
        return produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + produitId));
    }
    
    /**
     * Récupère les produits par catégorie
     * @param categorieId ID de la catégorie
     * @return Liste des produits de la catégorie
     * @throws EntityNotFoundException si la catégorie n'existe pas
     */
    public List<Produit> getProduitsParCategorie(int categorieId) {
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'ID: " + categorieId));
        return produitRepository.findByCategorieAndStockDisponibleGreaterThan(categorie, 0);
    }

}