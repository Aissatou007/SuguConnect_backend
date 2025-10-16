package odk.SuguConnect.DTO.Responses;

import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;

import java.time.LocalDate;

public record ProducteurResponseDTO(
        int id ,
        String nom ,
        String prenom ,
        String telephone ,
        String email ,
        String localisation ,
        long latitude ,
        long longitude ,
        Role role ,
        StatutProducteur statutProducteur ,
        String description,
        LocalDate dateInscription
) {
}
