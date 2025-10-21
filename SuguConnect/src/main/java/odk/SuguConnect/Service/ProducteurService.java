package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Mapper.ProducteurMapper;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service

public class ProducteurService {
    private final ProducteurRepository producteurRepository ;
    private final ProduitRepository produitRepository;
    private final ProduitService produitService;
    public ProducteurService(ProducteurRepository producteurRepository, ProduitRepository produitRepository, ProduitService produitService) {
        this.producteurRepository = producteurRepository;
        this.produitRepository = produitRepository;
        this.produitService = produitService;
    }

    //Inscription d'un producteur
    public String inscriptionProducteur(ProducteurRequestDTO producteurRequestDTO, String telephone){
        Producteur producteur =  producteurRepository.findByTelephone(telephone);
        if (producteur != null){
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
        Producteur producteur1 = ProducteurMapper.toEntity(producteurRequestDTO,new Producteur());
        producteur1.setRole(Role.PRODUCTEUR);
        producteur1.setStatutProducteur(StatutProducteur.EN_ATTENTE);
        producteur1.setDateInscription(LocalDate.now());
        producteurRepository.save(producteur1);
        return "Soyez le bienvenue ";
    }
    //Connexion d'un producteur
    public ProducteurResponseDTO connexionProducteur(String telephone , String motDePasse){
        Producteur producteur = producteurRepository.findByTelephone(telephone);
        if(producteur == null){
            throw new EntityNotFoundException("Produteur non trouver");
        }
        if(producteur.getStatutProducteur()==StatutProducteur.EN_ATTENTE || producteur.getStatutProducteur()==StatutProducteur.REFUSER){
            throw new IllegalArgumentException("Vous n'avez pas d'accès");
        } if(!producteur.getMotDePasse().equals(motDePasse) ){
            throw new IllegalArgumentException("Mot de passe incorrecte");
        } return ProducteurMapper.toResponse(producteur);

    }
    //Lister les producteurs
    public List<ProducteurResponseDTO> recupererLesProducteurs(){
        List<Producteur> producteurs = producteurRepository.findAll();
        return producteurs.stream().map(ProducteurMapper::toResponse).toList();
    }
    //Voir les informations d'un seul producteur
    public ProducteurResponseDTO recupererUnProducteur(int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        return ProducteurMapper.toResponse(producteur);

    }
    //Modifier les informations d'un producteur
    public String modifierInformationProducteur(ProducteurRequestDTO producteurRequestDTO,int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        producteur.setNom(producteurRequestDTO.nom());
        producteur.setPrenom(producteurRequestDTO.prenom());
        producteur.setTelephone(producteurRequestDTO.telephone());
        producteur.setEmail(producteurRequestDTO.email());
        producteur.setLocalisation(producteurRequestDTO.localisation());
        producteur.setMotDePasse(producteurRequestDTO.motDePasse());
        producteur.setDesription(producteurRequestDTO.description());
        producteurRepository.save(producteur);
        return "Vos informations ont été modifier avec succès";
    }
    //Supprimer un producteur
    public String supprimerProducteur(int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        producteurRepository.delete(producteur);
        return "Le compte a été supprimer avec succès";
    }

    public Produit ajouterProduit(Produit produit , int producteurId){
        return produitService.ajouterProduit(produit , producteurId);
    }
    public Produit modifierProduit(Produit produitModifie , int produitId , int producteurId){
       return produitService.modifierProduit(produitModifie, produitId , producteurId);
    }
    public List<Produit> listerLesProduits(int producteurId){
        return produitService.listerLesProduits(producteurId);
    }
    public String supprimerProduit(int produitId , int producteurId){
       produitService.supprimerProduit(produitId,producteurId);
       return "Le produit  a ete supprimer";
    }

}

