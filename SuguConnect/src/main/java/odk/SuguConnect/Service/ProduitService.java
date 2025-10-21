package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProduitService {
    private final ProduitRepository produitRepository ;
    private final ProducteurRepository producteurRepository ;

    public ProduitService(ProduitRepository produitRepository, ProducteurRepository producteurRepository) {
        this.produitRepository = produitRepository;
        this.producteurRepository = producteurRepository;
    }
    public Produit ajouterProduit(Produit produit , int producteurId){
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Ce producteur n'existe pas"));
        if(producteur.getStatutProducteur() != StatutProducteur.ACCEPTER){
            throw new IllegalStateException("Vous n'avez pas de droit pour ajouter un produit");
        }
        if(produit.getPhotos().isEmpty()){
            throw new IllegalArgumentException("Le produit doit contenir au moins une photo");
        }
        if(produit.getPhotos().size() > 4){
            throw new IllegalArgumentException("Le produit ne peux pas avoir plus de 4 photos");
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
        }produit.setNom(produitModifie.getNom());
        produit.setQuantite(produitModifie.getQuantite());
        produit.setDescription(produitModifie.getDescription());
        produit.setPrixUnitaire(produitModifie.getPrixUnitaire());
        produit.setUnite(produitModifie.getUnite());
        produit.setStockDisponible(produitModifie.getQuantite());
        produitRepository.save(produitModifie);
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
        return "Le produit  a ete supprimer";
    }

}
