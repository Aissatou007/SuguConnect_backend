package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Mapper.ConsommateurMapper;
import odk.SuguConnect.Repository.ConsommateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsommateurService {
    private ConsommateurRepository  consommateurRepository;
    private ConsommateurRequestDTO consommateurRequestDTO ;
    private ConsommateurResponseDTO consommateurResponseDTO ;

    //Inscription d'un consommateur
    public String inscriptionConsommateur(ConsommateurRequestDTO consommateurRequestDTO, String telephone){
        Consommateur conso =  consommateurRepository.findByTelephone(telephone);
        if (conso != null){
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
        Consommateur consommateur = ConsommateurMapper.toEntity(consommateurRequestDTO,new Consommateur());
        consommateur.setRole(Role.CONSOMMATEUR);
        consommateur.setDateInscription(LocalDate.now());
        Panier panier = new Panier();
        panier.setConsommateur(consommateur);
        consommateur.setPanier(panier);
        consommateurRepository.save(consommateur);
        return "Soyez le bienvenue ";
    }
    public List<ConsommateurResponseDTO> recupererLesConsommateurs(){
        List<Consommateur> consommateurs = consommateurRepository.findAll();
        return consommateurs.stream().map(ConsommateurMapper::toResponse).toList();
    }
    public ConsommateurResponseDTO recupererUnConsommateur(int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        return ConsommateurMapper.toResponse(consommateur);

    }
    public String modifierInformationConsommateur(ConsommateurRequestDTO consommateurRequestDTO,int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateur.setNom(consommateurRequestDTO.nom());
        consommateur.setPrenom(consommateurRequestDTO.prenom());
        consommateur.setTelephone(consommateurRequestDTO.telephone());
        consommateur.setEmail(consommateurRequestDTO.email());
        consommateur.setLocalisation(consommateurRequestDTO.localisation());
        consommateur.setMotDePasse(consommateurRequestDTO.motDePasse());
        consommateurRepository.save(consommateur);
        return "Vos informations ont été modifier avec succès";
    }
    public String supprimerConsommateur(int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateurRepository.delete(consommateur);
        return "Le compte a été supprimer avec succès";
    }

}
