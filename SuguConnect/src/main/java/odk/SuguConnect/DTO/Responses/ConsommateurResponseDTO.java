package odk.SuguConnect.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.Role;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Informations d'un consommateur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsommateurResponseDTO {
    @Schema(description = "ID du consommateur", example = "1")
    private int id;
    
    @Schema(description = "Nom du consommateur", example = "Dembele")
    private String nom;
    
    @Schema(description = "Prénom du consommateur", example = "Adama")
    private String prenom;
    
    @Schema(description = "Numéro de téléphone", example = "94907946")
    private String telephone;
    
    @Schema(description = "Email du consommateur", example = "adama@example.com")
    private String email;
    
    @Schema(description = "Localisation du consommateur", example = "Bamako")
    private String localisation;
    
    @Schema(description = "Latitude de la position")
    private long latitude;
    
    @Schema(description = "Longitude de la position")
    private long longitude;
    
    @Schema(description = "Rôle de l'utilisateur")
    private Role role;
    
    @Schema(description = "ID du panier associé", example = "1")
    private Integer panierId;
    
    @Schema(description = "Date d'inscription")
    private LocalDate dateInscription;
}
