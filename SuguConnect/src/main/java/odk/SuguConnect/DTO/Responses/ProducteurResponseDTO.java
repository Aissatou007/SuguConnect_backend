package odk.SuguConnect.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Informations d'un producteur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProducteurResponseDTO {
    @Schema(description = "ID du producteur", example = "1")
    private int id;
    
    @Schema(description = "Nom du producteur", example = "Traoré")
    private String nom;
    
    @Schema(description = "Prénom du producteur", example = "Mamadou")
    private String prenom;
    
    @Schema(description = "Numéro de téléphone", example = "76543210")
    private String telephone;
    
    @Schema(description = "Email du producteur", example = "mamadou@example.com")
    private String email;
    
    @Schema(description = "Localisation du producteur", example = "Bamako")
    private String localisation;
    
    @Schema(description = "Latitude de la position")
    private long latitude;
    
    @Schema(description = "Longitude de la position")
    private long longitude;
    
    @Schema(description = "Rôle de l'utilisateur")
    private Role role;
    
    @Schema(description = "Statut de validation du producteur")
    private StatutProducteur statutProducteur;
    
    @Schema(description = "Description de l'activité", example = "Producteur de fruits biologiques")
    private String description;
    
    @Schema(description = "Nom de la ferme", example = "Ferme Bio de Mamadou")
    private String nomFerme;
    
    @Schema(description = "URL de la photo de profil")
    private String photoUrl;
    
    @Schema(description = "Date d'inscription")
    private LocalDate dateInscription;
    
    @Schema(description = "Motif de rejet (si refusé)")
    private String motifDeRejet;
}
