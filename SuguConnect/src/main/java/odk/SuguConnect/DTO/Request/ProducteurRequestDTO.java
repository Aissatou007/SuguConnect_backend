package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO pour l'inscription/modification d'un producteur")
public class ProducteurRequestDTO {
    @Schema(description = "Nom du producteur", example = "Diallo")
    private String nom;
    
    @Schema(description = "Prénom du producteur", example = "Amadou")
    private String prenom;
    
    @Schema(description = "Numéro de téléphone", example = "77123456")
    private String telephone;
    
    @Schema(description = "Adresse email", example = "amadou@ferme.com")
    private String email;
    
    @Schema(description = "Localisation", example = "Conakry")
    private String localisation;
    
    @Schema(description = "Latitude")
    private long latitude;
    
    @Schema(description = "Longitude")
    private long longitude;
    
    @Schema(description = "Mot de passe")
    private String motDePasse;
    
    @Schema(description = "Description de l'activité")
    private String description;
    
    @Schema(description = "Nom de la ferme", example = "Ferme Bio d'Amadou")
    private String nomFerme;
}
