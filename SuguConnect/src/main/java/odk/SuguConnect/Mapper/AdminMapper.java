package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Entity.Consommateur;

public class AdminMapper {
    public static Admin toEntity(AdminRequestDTO dtoAdmin, Admin admin){
        admin.setNom(dtoAdmin.nom());
        admin.setPrenom(dtoAdmin.prenom());
        admin.setTelephone(dtoAdmin.telephone());
        admin.setEmail(dtoAdmin.email());
        admin.setLocalisation(dtoAdmin.localisation());
        admin.setLattitude(dtoAdmin.latitude());
        admin.setLongitude(dtoAdmin.longitude());
        admin.setMotDePasse(dtoAdmin.motDePasse());
        return admin;
    }

    public static AdminResponseDTO toResponse(Admin admin){
        if(admin == null) return null;

        AdminResponseDTO adminResponseDTO = new AdminResponseDTO(
                admin.getId(),
                admin.getNom(),
                admin.getPrenom(),
                admin.getTelephone(),
                admin.getEmail(),
                admin.getLocalisation(),
                admin.getLattitude(),
                admin.getLongitude(),
                admin.getRole(),
                admin.getDateInscription()
        );
        return adminResponseDTO;
    }
}
