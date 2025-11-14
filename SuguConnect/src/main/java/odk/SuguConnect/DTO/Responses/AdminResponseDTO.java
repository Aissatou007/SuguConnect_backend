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
@Schema(description = "Informations d'un administrateur")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminResponseDTO {
    @Schema(description = "ID de l'administrateur", example = "1")
    private int id;
    
    @Schema(description = "Nom de l'administrateur", example = "Super")
    private String nom;
    
    @Schema(description = "Prénom de l'administrateur", example = "Admin")
    private String prenom;
    
    @Schema(description = "Numéro de téléphone", example = "70000000")
    private String telephone;
    
    @Schema(description = "Email de l'administrateur", example = "admin@suguconnect.com")
    private String email;
    
    @Schema(description = "Localisation de l'administrateur")
    private String localisation;
    
    @Schema(description = "Latitude de la position")
    private long latitude;
    
    @Schema(description = "Longitude de la position")
    private long longitude;
    
    @Schema(description = "Rôle de l'utilisateur")
    private Role role;
    
    @Schema(description = "Date d'inscription")
    private LocalDate dateInscription;
}
