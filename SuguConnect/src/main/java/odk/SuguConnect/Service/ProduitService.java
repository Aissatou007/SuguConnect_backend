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

import java.util.List;
@Service
public class ProduitService {
    private final ProduitRepository produitRepository ;
    private final ProducteurRepository producteurRepository ;
    private final CategorieRepository categorieRepository;

    public ProduitService(ProduitRepository produitRepository, 
                         ProducteurRepository producteurRepository,
                         CategorieRepository categorieRepository) {
        this.produitRepository = produitRepository;
        this.producteurRepository = producteurRepository;
        this.categorieRepository = categorieRepository;
    }
    public Produit ajouterProduit(Produit produit , int producteurId){
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
        
        // Mettre à jour uniquement les champs non null
        if(produitModifie.getNom() != null) {
            produit.setNom(produitModifie.getNom());
        }
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
    public List<Produit> listerLesProduits(int producteurId){
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(()->new EntityNotFoundException("Ce producteur n'existe pas"));
        return produitRepository.findByProducteur(producteur);
    }
    public String supprimerProduit(int produitId , int producteurId){
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(()->new EntityNotFoundException("Ce produit n'existe pas"));
        if(produit.getProducteur().getId() != producteurId){
            throw new IllegalArgumentException("Vous ne pouvez pas modifier ce produit");
        } produitRepository.delete(produit);
        return "Le produit a été supprimé";
    }

}
