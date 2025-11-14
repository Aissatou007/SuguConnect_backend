package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO simplifié pour un consommateur")
public class ConsommateurSimpleDTO {
    @Schema(description = "ID du consommateur", example = "1")
    private int id;
    
    @Schema(description = "Nom du consommateur", example = "Diabaté")
    private String nom;
    
    @Schema(description = "Prénom du consommateur", example = "Aïda")
    private String prenom;
    
    @Schema(description = "Téléphone du consommateur", example = "00000000")
    private String telephone;
    
    @Schema(description = "Email du consommateur", example = "aida.diabate@example.com")
    private String email;
    
    @Schema(description = "Localisation du consommateur", example = "Bamako, Mali")
    private String localisation;
    
    @Schema(description = "Longitude", example = "-7")
    private long longitude;
    
    @Schema(description = "Latitude", example = "12")
    private long latitude;
    
    @Schema(description = "Rôle de l'utilisateur", example = "CONSOMMATEUR")
    private Role role;
}