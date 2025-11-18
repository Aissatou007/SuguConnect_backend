package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import odk.SuguConnect.DTO.Responses.ProduitsParCategorieDTO;
import odk.SuguConnect.DTO.Responses.ProduitSimpleDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProduitService {
    private final ProduitRepository produitRepository ;
    private final ProducteurRepository producteurRepository ;
    private final CategorieRepository categorieRepository;

    // Liste noire : produits interdits
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

    public Produit ajouterProduit(Produit produit, int producteurId) {
        // Validation des champs obligatoires
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        
        if (produit.getDescription() == null || produit.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description du produit est obligatoire");
        }
        
        if (produit.getPrixUnitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être supérieur à zéro");
        }
        
        if (produit.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }
        
        if (produit.getUnite() == null) {
            throw new IllegalArgumentException("L'unité de mesure est obligatoire");
        }
        
        if (produit.getCategorie() == null || produit.getCategorie().getId() <= 0) {
            throw new IllegalArgumentException("La catégorie est obligatoire");
        }
        
        // Validation du champ estBio (obligatoire)
        // Note: No need to validate estBio as it's a boolean with a default value in the entity
        
        if (produit.getPhotos() == null || produit.getPhotos().isEmpty()) {
            throw new IllegalArgumentException("Au moins une photo est requise");
        }
        
        if (produit.getPhotos().size() > 4) {
            throw new IllegalArgumentException("Maximum 4 photos autorisées");
        }
        
        // Validation du nom du produit
        validerNomProduit(produit.getNom());
        
        // Associer le producteur
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur introuvable"));
        produit.setProducteur(producteur);
        
        // Associer la catégorie
        Categorie categorie = categorieRepository.findById(produit.getCategorie().getId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
        produit.setCategorie(categorie);
        
        // Initialiser le stock disponible
        produit.setStockDisponible(produit.getQuantite());
        
        return produitRepository.save(produit);
    }
    
    /**
     * Ajoute un produit avec un processus amélioré permettant de choisir d'abord la catégorie
     * puis de sélectionner ou entrer le nom du produit
     */
    public Produit ajouterProduitAmeliore(Produit produit, int producteurId, int categorieId) {
        // Validation des champs obligatoires
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        
        if (produit.getDescription() == null || produit.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description du produit est obligatoire");
        }
        
        if (produit.getPrixUnitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être supérieur à zéro");
        }
        
        if (produit.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }
        
        if (produit.getUnite() == null) {
            throw new IllegalArgumentException("L'unité de mesure est obligatoire");
        }
        
        // Validation du champ estBio (obligatoire)
        // Note: No need to validate estBio as it's a boolean with a default value in the entity
        
        if (produit.getPhotos() == null || produit.getPhotos().isEmpty()) {
            throw new IllegalArgumentException("Au moins une photo est requise");
        }
        
        if (produit.getPhotos().size() > 4) {
            throw new IllegalArgumentException("Maximum 4 photos autorisées");
        }
        
        // Validation du nom du produit
        validerNomProduit(produit.getNom());
        
        // Associer le producteur
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur introuvable"));
        produit.setProducteur(producteur);
        
        // Associer la catégorie
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable"));
        produit.setCategorie(categorie);
        
        // Initialiser le stock disponible
        produit.setStockDisponible(produit.getQuantite());
        
        return produitRepository.save(produit);
    }
    
    /**
     * Récupère les produits existants dans une catégorie pour permettre
     * au producteur de choisir parmi eux lors de la création
     */
    public ProduitsParCategorieDTO getProduitsParCategorie(int categorieId) {
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new EntityNotFoundException("Cette catégorie n'existe pas"));
        
        List<Produit> produits = produitRepository.findByCategorieId(categorieId);
        List<ProduitSimpleDTO> produitDtos = produits.stream()
                .map(produit -> new ProduitSimpleDTO(
                        produit.getId(),
                        produit.getNom(),
                        produit.getPrixUnitaire(),
                        produit.getProducteur() != null ? produit.getProducteur().getId() : 0,
                        produit.getProducteur() != null ? produit.getProducteur().getNom() : "",
                        produit.getProducteur() != null ? produit.getProducteur().getPrenom() : ""
                ))
                .toList();
        
        return new ProduitsParCategorieDTO(
                categorie.getId(),
                categorie.getLibelle(),
                produitDtos
        );
    }
    
    /**
     * Récupère tous les produits existants d'un producteur pour une catégorie spécifique
     */
    public List<ProduitSimpleDTO> getProduitsDuProducteurParCategorie(int producteurId, int categorieId) {
        List<Produit> produits = produitRepository.findByProducteurIdAndCategorieId(producteurId, categorieId);
        return produits.stream()
                .map(produit -> new ProduitSimpleDTO(
                        produit.getId(),
                        produit.getNom(),
                        produit.getPrixUnitaire(),
                        produit.getProducteur() != null ? produit.getProducteur().getId() : 0,
                        produit.getProducteur() != null ? produit.getProducteur().getNom() : "",
                        produit.getProducteur() != null ? produit.getProducteur().getPrenom() : ""
                ))
                .toList();
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
        // Mettre à jour le champ estBio si fourni
        produit.setEstBio(produitModifie.isEstBio());
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
    public String supprimerTousLesProduits() {
        long count = produitRepository.count();
        produitRepository.deleteAll();
        return count + " produits ont été supprimés";
    }
    
    private void validerNomProduit(String nomProduit) {
        if (nomProduit == null || nomProduit.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        
        String nomNormalise = nomProduit.trim().toLowerCase();

        for (String produitInterdit : produitsInterdits) {
            if (nomNormalise.contains(produitInterdit)) {
                throw new IllegalArgumentException(
                    "Le nom du produit '" + nomProduit + "' n'est pas autorisé. " +
                    "Veuillez choisir un autre nom.");
            }
        }
    }
}