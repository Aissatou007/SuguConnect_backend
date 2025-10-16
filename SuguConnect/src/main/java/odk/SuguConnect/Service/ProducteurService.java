package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Mapper.ProducteurMapper;
import odk.SuguConnect.Repository.ProducteurRepository;


import java.time.LocalDate;
import java.util.List;

public class ProducteurService {
    private ProducteurRepository producteurRepository ;
    private ProducteurRequestDTO producteurRequestDTO ;
    private ProducteurResponseDTO producteurResponseDTO;

    //Inscription d'un producteur
    public String inscriptionConsommateur(ProducteurRequestDTO producteurRequestDTO, String telephone){
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
    public List<ProducteurResponseDTO> recupererLesProducteurs(){
        List<Producteur> producteurs = producteurRepository.findAll();
        return producteurs.stream().map(ProducteurMapper::toResponse).toList();
    }
    public ProducteurResponseDTO recupererUnProducteur(int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        return ProducteurMapper.toResponse(producteur);

    }
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
    public String supprimerProducteur(int id){
        Producteur producteur = producteurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
        producteurRepository.delete(producteur);
        return "Le compte a été supprimer avec succès";
    }
}
