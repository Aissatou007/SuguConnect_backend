package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.LoginRequest;
import odk.SuguConnect.DTO.Responses.AuthResponse;
import odk.SuguConnect.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API de gestion de l'authentification et des tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Connexion universelle",
            description = "Permet à n'importe quel utilisateur (Admin, Producteur, Consommateur) de se connecter avec son téléphone et mot de passe"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie - Token JWT généré",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Mot de passe incorrect"),
            @ApiResponse(responseCode = "403", description = "Compte désactivé ou en attente de validation"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants de connexion (téléphone et mot de passe)",
                    required = true
            )
            @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la connexion : " + e.getMessage()));
        }
    }

    @PostMapping("/login/admin")
    @Operation(
            summary = "Connexion administrateur",
            description = "Endpoint spécifique pour la connexion des administrateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion admin réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Identifiants incorrects"),
            @ApiResponse(responseCode = "404", description = "Compte administrateur non trouvé")
    })
    public ResponseEntity<?> loginAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants administrateur",
                    required = true
            )
            @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.loginAdmin(loginRequest);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la connexion admin : " + e.getMessage()));
        }
    }

    @PostMapping("/login/producteur")
    @Operation(
            summary = "Connexion producteur",
            description = "Endpoint spécifique pour la connexion des producteurs. Le compte doit être validé par un admin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion producteur réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Mot de passe incorrect"),
            @ApiResponse(responseCode = "403", description = "Compte en attente de validation ou refusé"),
            @ApiResponse(responseCode = "404", description = "Compte producteur non trouvé")
    })
    public ResponseEntity<?> loginProducteur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants producteur",
                    required = true
            )
            @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.loginProducteur(loginRequest);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la connexion producteur : " + e.getMessage()));
        }
    }

    @PostMapping("/login/consommateur")
    @Operation(
            summary = "Connexion consommateur",
            description = "Endpoint spécifique pour la connexion des consommateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion consommateur réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Mot de passe incorrect"),
            @ApiResponse(responseCode = "404", description = "Compte consommateur non trouvé")
    })
    public ResponseEntity<?> loginConsommateur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants consommateur",
                    required = true
            )
            @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.loginConsommateur(loginRequest);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la connexion consommateur : " + e.getMessage()));
        }
    }

    @PostMapping("/validate-token")
    @Operation(
            summary = "Valider un token JWT",
            description = "Vérifie si un token JWT est valide et non expiré"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validé avec succès"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    public ResponseEntity<?> validateToken(
            @RequestParam String token,
            @RequestParam String telephone) {
        try {
            boolean isValid = authService.validateToken(token, telephone);
            
            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("message", "Token valide");
                response.put("role", authService.extractRole(token));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Token invalide ou expiré"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Erreur de validation du token : " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtenir les informations de l'utilisateur connecté",
            description = "Récupère les informations de l'utilisateur à partir du token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations utilisateur récupérées"),
            @ApiResponse(responseCode = "401", description = "Token manquant ou invalide")
    })
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Token manquant ou format invalide"));
            }
            
            String token = authHeader.substring(7);
            String telephone = authService.extractTelephone(token);
            String role = authService.extractRole(token);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("telephone", telephone);
            userInfo.put("role", role);
            userInfo.put("message", "Utilisateur authentifié");
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Token invalide : " + e.getMessage()));
        }
    }

    /**
     * Méthode utilitaire pour créer une réponse d'erreur standardisée
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }
}
