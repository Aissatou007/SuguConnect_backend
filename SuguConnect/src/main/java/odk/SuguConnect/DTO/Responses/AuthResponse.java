package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Role;

@Schema(description = "Réponse d'authentification avec token JWT")
public record AuthResponse(
        @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        
        @Schema(description = "Type de token", example = "Bearer")
        String tokenType,
        
        @Schema(description = "ID de l'utilisateur")
        int userId,
        
        @Schema(description = "Nom de l'utilisateur")
        String nom,
        
        @Schema(description = "Prénom de l'utilisateur")
        String prenom,
        
        @Schema(description = "Email de l'utilisateur")
        String email,
        
        @Schema(description = "Téléphone de l'utilisateur")
        String telephone,
        
        @Schema(description = "Rôle de l'utilisateur")
        Role role,
        
        @Schema(description = "Message de bienvenue")
        String message
) {
    public AuthResponse(String token, int userId, String nom, String prenom, String email, String telephone, Role role) {
        this(token, "Bearer", userId, nom, prenom, email, telephone, role, "Connexion réussie");
    }
}
