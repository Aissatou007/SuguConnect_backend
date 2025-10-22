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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service

public class ProducteurService {
    private final ProducteurRepository producteurRepository ;
    private final ProduitRepository produitRepository;
    private final ProduitService produitService;
    private final PasswordEncoder passwordEncoder;
    
    public ProducteurService(ProducteurRepository producteurRepository, 
                            ProduitRepository produitRepository, 
                            ProduitService produitService,
                            PasswordEncoder passwordEncoder) {
        this.producteurRepository = producteurRepository;
        this.produitRepository = produitRepository;
        this.produitService = produitService;
        this.passwordEncoder = passwordEncoder;
    }

    //Inscription d'un producteur
    public String inscriptionProducteur(ProducteurRequestDTO producteurRequestDTO, String telephone){
        Producteur producteur =  producteurRepository.findByTelephone(telephone);
        if (producteur != null){
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
        Producteur producteur1 = ProducteurMapper.toEntity(producteurRequestDTO,new Producteur());
        producteur1.setMotDePasse(passwordEncoder.encode(producteurRequestDTO.motDePasse()));
        producteur1.setRole(Role.PRODUCTEUR);
        producteur1.setStatutProducteur(StatutProducteur.EN_ATTENTE);
        producteur1.setDateInscription(LocalDate.now());
        producteur1.setActif(true);
        producteurRepository.save(producteur1);
        return "Soyez le bienvenue ";
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
        // Encoder le mot de passe uniquement s'il est fourni et non vide
        if (producteurRequestDTO.motDePasse() != null && !producteurRequestDTO.motDePasse().isEmpty()) {
            producteur.setMotDePasse(passwordEncoder.encode(producteurRequestDTO.motDePasse()));
        }
        producteur.setDescription(producteurRequestDTO.description());
        producteurRepository.save(producteur);
        return "Vos informations ont été modifiées avec succès";
    }
    //Supprimer un producteur
    public String supprimerProducteur(int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        producteurRepository.delete(producteur);
        return "Le compte a été supprimé avec succès";
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
       return "Le produit a été supprimé";
    }

}

