package odk.SuguConnect.DTO.Responses;

import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Enums.Role;

import java.time.LocalDate;

public record AdminResponseDTO(
        int id ,
        String nom ,
        String prenom ,
        String telephone ,
        String email ,
        String localisation ,
        long latitude ,
        long longitude ,
        Role role ,
        LocalDate dateInscription
) {
}
