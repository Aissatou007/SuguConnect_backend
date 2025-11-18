package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Requête de connexion")
public class LoginRequest {
    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "70000000", required = true)
    private String telephone;
    
    @Schema(description = "Mot de passe", example = "password123", required = true)
    private String motDePasse;
}
