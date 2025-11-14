package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Réponse d'authentification avec token JWT")
public class AuthResponse {
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Type de token", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "ID de l'utilisateur")
    private int userId;
    
    @Schema(description = "Nom de l'utilisateur")
    private String nom;
    
    @Schema(description = "Prénom de l'utilisateur")
    private String prenom;
    
    @Schema(description = "Email de l'utilisateur")
    private String email;
    
    @Schema(description = "Téléphone de l'utilisateur")
    private String telephone;
    
    @Schema(description = "Rôle de l'utilisateur")
    private Role role;
    
    @Schema(description = "Message de bienvenue")
    private String message;
    
    public AuthResponse(String token, int userId, String nom, String prenom, String email, String telephone, Role role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.message = "Connexion réussie";
    }
}
