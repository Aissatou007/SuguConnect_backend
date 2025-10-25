package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.LivreurRequestDTO;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Entity.Livreur;

public class LivreurMapper {
    
    public static Livreur toEntity(LivreurRequestDTO dto, Livreur livreur) {
        livreur.setNom(dto.nom());
        livreur.setPrenom(dto.prenom());
        livreur.setTelephone(dto.telephone());
        livreur.setEmail(dto.email());
        livreur.setLocalisation(dto.localisation());
        livreur.setMatricule(dto.matricule());
        livreur.setVehicule(dto.vehicule());
        return livreur;
    }
    
    public static LivreurResponseDTO toResponse(Livreur livreur) {
        return LivreurResponseDTO.builder()
                .id(livreur.getId())
                .nom(livreur.getNom())
                .prenom(livreur.getPrenom())
                .telephone(livreur.getTelephone())
                .email(livreur.getEmail())
                .localisation(livreur.getLocalisation())
                .matricule(livreur.getMatricule())
                .vehicule(livreur.getVehicule())
                .disponible(livreur.isDisponible())
                .build();
    }
}