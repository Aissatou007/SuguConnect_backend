package odk.SuguConnect.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;

import java.time.LocalDate;

@Schema(description = "Informations d'un producteur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducteurResponseDTO(
        @Schema(description = "ID du producteur", example = "1")
        int id,
        
        @Schema(description = "Nom du producteur", example = "Traoré")
        String nom,
        
        @Schema(description = "Prénom du producteur", example = "Mamadou")
        String prenom,
        
        @Schema(description = "Numéro de téléphone", example = "76543210")
        String telephone,
        
        @Schema(description = "Email du producteur", example = "mamadou@example.com")
        String email,
        
        @Schema(description = "Localisation du producteur", example = "Bamako")
        String localisation,
        
        @Schema(description = "Latitude de la position")
        long latitude,
        
        @Schema(description = "Longitude de la position")
        long longitude,
        
        @Schema(description = "Rôle de l'utilisateur")
        Role role,
        
        @Schema(description = "Statut de validation du producteur")
        StatutProducteur statutProducteur,
        
        @Schema(description = "Description de l'activité", example = "Producteur de fruits biologiques")
        String description,
        
        @Schema(description = "Nom de la ferme", example = "Ferme Bio de Mamadou")
        String nomFerme,
        
        @Schema(description = "URL de la photo de profil")
        String photoUrl,
        
        @Schema(description = "Date d'inscription")
        LocalDate dateInscription,
        
        @Schema(description = "Motif de rejet (si refusé)")
        String motifDeRejet
) {
}
