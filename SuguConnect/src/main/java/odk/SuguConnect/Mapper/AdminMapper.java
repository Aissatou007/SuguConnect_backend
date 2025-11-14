package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Entity.Consommateur;

public class AdminMapper {
    public static Admin toEntity(AdminRequestDTO dtoAdmin, Admin admin){
        admin.setNom(dtoAdmin.getNom());
        admin.setPrenom(dtoAdmin.getPrenom());
        admin.setTelephone(dtoAdmin.getTelephone());
        admin.setEmail(dtoAdmin.getEmail());
        admin.setLocalisation(dtoAdmin.getLocalisation());
        admin.setLatitude(dtoAdmin.getLatitude());
        admin.setLongitude(dtoAdmin.getLongitude());
        admin.setMotDePasse(dtoAdmin.getMotDePasse());
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
                admin.getLatitude(),
                admin.getLongitude(),
                admin.getRole(),
                admin.getDateInscription()
        );
        return adminResponseDTO;
    }
}
