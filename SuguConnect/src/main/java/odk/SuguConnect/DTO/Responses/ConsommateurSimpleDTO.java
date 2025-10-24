package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Role;

@Schema(description = "DTO simplifié pour un consommateur")
public record ConsommateurSimpleDTO(
        @Schema(description = "ID du consommateur", example = "1")
        int id,
        
        @Schema(description = "Nom du consommateur", example = "Diabaté")
        String nom,
        
        @Schema(description = "Prénom du consommateur", example = "Aïda")
        String prenom,
        
        @Schema(description = "Téléphone du consommateur", example = "00000000")
        String telephone,
        
        @Schema(description = "Email du consommateur", example = "aida.diabate@example.com")
        String email,
        
        @Schema(description = "Localisation du consommateur", example = "Bamako, Mali")
        String localisation,
        
        @Schema(description = "Longitude", example = "-7")
        long longitude,
        
        @Schema(description = "Latitude", example = "12")
        long latitude,
        
        @Schema(description = "Rôle de l'utilisateur", example = "CONSOMMATEUR")
        Role role
) {
}