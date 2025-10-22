package odk.SuguConnect.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Role;

import java.time.LocalDate;

@Schema(description = "Informations d'un administrateur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdminResponseDTO(
        @Schema(description = "ID de l'administrateur", example = "1")
        int id,
        
        @Schema(description = "Nom de l'administrateur", example = "Super")
        String nom,
        
        @Schema(description = "Prénom de l'administrateur", example = "Admin")
        String prenom,
        
        @Schema(description = "Numéro de téléphone", example = "70000000")
        String telephone,
        
        @Schema(description = "Email de l'administrateur", example = "admin@suguconnect.com")
        String email,
        
        @Schema(description = "Localisation de l'administrateur")
        String localisation,
        
        @Schema(description = "Latitude de la position")
        long latitude,
        
        @Schema(description = "Longitude de la position")
        long longitude,
        
        @Schema(description = "Rôle de l'utilisateur")
        Role role,
        
        @Schema(description = "Date d'inscription")
        LocalDate dateInscription
) {
}
