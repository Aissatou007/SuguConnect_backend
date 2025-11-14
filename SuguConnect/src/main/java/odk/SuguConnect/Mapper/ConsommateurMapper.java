package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Consommateur;

public class ConsommateurMapper {
    public static Consommateur toEntity(ConsommateurRequestDTO dtoConsommateur, Consommateur consommateur){
        consommateur.setNom(dtoConsommateur.getNom());
        consommateur.setPrenom(dtoConsommateur.getPrenom());
        consommateur.setTelephone(dtoConsommateur.getTelephone());
        consommateur.setEmail(dtoConsommateur.getEmail());
        consommateur.setLocalisation(dtoConsommateur.getLocalisation());
        consommateur.setLatitude(dtoConsommateur.getLatitude());
        consommateur.setLongitude(dtoConsommateur.getLongitude());
        consommateur.setMotDePasse(dtoConsommateur.getMotDePasse());
        return consommateur;
    }

    public static ConsommateurResponseDTO toResponse(Consommateur consommateur){
        if(consommateur == null) return null;

        ConsommateurResponseDTO consommateurResponseDTO = new ConsommateurResponseDTO(
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
                consommateur.getDateInscription()
        );
        return consommateurResponseDTO;
    }
}
