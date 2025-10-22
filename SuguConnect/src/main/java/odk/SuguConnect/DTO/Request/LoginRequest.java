package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête de connexion")
public record LoginRequest(
        @Schema(description = "Numéro de téléphone de l'utilisateur", example = "70000000", required = true)
        String telephone,
        
        @Schema(description = "Mot de passe", example = "password123", required = true)
        String motDePasse
) {
}
