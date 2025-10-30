package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProduitService {
    private final ProduitRepository produitRepository ;
    private final ProducteurRepository producteurRepository ;
    private final CategorieRepository categorieRepository;

    // Liste noire : produits interdits (non agricoles)
    private final List<String> produitsInterdits = Arrays.asList(
            "téléphone", "telephone", "voiture", "ordinateur", "pc",
            "tv", "télé", "chaussure", "vetement", "parfum", "montre", "casque",
            "laptop", "smartphone", "vélo", "bicyclette", "bijoux", "lunettes",
            "sac", "portable", "tablette", "console", "jeux", "vêtements",
            "cosmétiques", "maquillage", "jouet", "toys", "meubles", "furniture",
            "électroménager", "appareil", "machine", "outil", "tools",
            "médicament", "medicine", "pharmacie", "drug", "book", "livre",
            "magazine", "journal", "cd", "dvd", "film", "music", "musique",
            "instrument", "musical", "sport", "fitness", "gym", "beauté", "beauty"
    );

    public ProduitService(ProduitRepository produitRepository, 
                         ProducteurRepository producteurRepository,
                         CategorieRepository categorieRepository) {
        this.produitRepository = produitRepository;
        this.producteurRepository = producteurRepository;
        this.categorieRepository = categorieRepository;
    }

    public Produit ajouterProduit(Produit produit , int producteurId){
        // Valider le nom du produit
        validerNomProduit(produit.getNom());
        
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Ce producteur n'existe pas"));
        if(producteur.getStatutProducteur() != StatutProducteur.ACCEPTE){
            throw new IllegalStateException("Vous n'avez pas de droit pour ajouter un produit");
        }
        if(produit.getPhotos() == null || produit.getPhotos().isEmpty()){
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
        
        // Mettre à jour uniquement les champs non null
        if(produitModifie.getDescription() != null) {
            produit.setDescription(produitModifie.getDescription());
        }
        if(produitModifie.getPrixUnitaire() > 0) {
            produit.setPrixUnitaire(produitModifie.getPrixUnitaire());
        }
        if(produitModifie.getUnite() != null) {
            produit.setUnite(produitModifie.getUnite());
        }
        if(produitModifie.getQuantite() > 0) {
            produit.setQuantite(produitModifie.getQuantite());
            produit.setStockDisponible(produitModifie.getQuantite());
        }
        // Mettre à jour les photos uniquement si elles sont fournies
        if(produitModifie.getPhotos() != null && !produitModifie.getPhotos().isEmpty()) {
            if(produitModifie.getPhotos().size() > 4){
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
    
    // ========== Méthodes privées utilitaires ==========

    private void validerNomProduit(String nomProduit) {
        if (nomProduit == null || nomProduit.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        
        String nomNormalise = nomProduit.trim().toLowerCase();
        
        // Vérifier si le nom du produit figure dans la liste noire
        for (String produitInterdit : produitsInterdits) {
            if (nomNormalise.contains(produitInterdit)) {
                throw new IllegalArgumentException(
                    "Le nom du produit '" + nomProduit + "' n'est pas autorisé. " +
                    "Veuillez choisir un autre nom.");
            }
        }
    }
}