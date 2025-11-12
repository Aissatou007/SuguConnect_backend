package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Consommateur;

public class ConsommateurMapper {
    public static Consommateur toEntity(ConsommateurRequestDTO dtoConsommateur, Consommateur consommateur){
        consommateur.setNom(dtoConsommateur.nom());
        consommateur.setPrenom(dtoConsommateur.prenom());
        consommateur.setTelephone(dtoConsommateur.telephone());
        consommateur.setEmail(dtoConsommateur.email());
        consommateur.setLocalisation(dtoConsommateur.localisation());
        consommateur.setLatitude(dtoConsommateur.latitude());
        consommateur.setLongitude(dtoConsommateur.longitude());
        consommateur.setMotDePasse(dtoConsommateur.motDePasse());
        return consommateur;
    }

    public static ConsommateurResponseDTO toResponse(Consommateur consommateur){
        if(consommateur == null) return null;

        // On récupère le nombre de commandes si c'est disponible
        int nombreCommandes = consommateur.getCommandes() != null ? consommateur.getCommandes().size() : 0;

        return new ConsommateurResponseDTO(
                consommateur.getId(),
                consommateur.getNom(),
                consommateur.getPrenom(),
                consommateur.getTelephone(),
                consommateur.getEmail(),
                consommateur.getLocalisation(),
                consommateur.getLatitude(),
                consommateur.getLongitude(),
                consommateur.getRole(),
                consommateur.getPanier() != null ? consommateur.getPanier().getId() : null,
                consommateur.getDateInscription(),
                nombreCommandes
        );
    }
}
