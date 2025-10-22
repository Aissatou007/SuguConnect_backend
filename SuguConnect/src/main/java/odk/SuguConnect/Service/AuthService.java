package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.LoginRequest;
import odk.SuguConnect.DTO.Responses.AuthResponse;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.AdminRepository;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.UtilisateurRepository;
import odk.SuguConnect.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final ConsommateurRepository consommateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Authentifier un utilisateur (Admin, Producteur ou Consommateur)
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // Rechercher l'utilisateur par téléphone
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByTelephone(loginRequest.telephone());
        
        if (utilisateurOpt.isEmpty()) {
            throw new EntityNotFoundException("Aucun compte trouvé avec ce numéro de téléphone");
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        // Vérifier le mot de passe
        if (!passwordEncoder.matches(loginRequest.motDePasse(), utilisateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        // Vérifier si le compte est actif
        if (!utilisateur.isActif()) {
            throw new IllegalStateException("Votre compte est désactivé. Veuillez contacter l'administrateur.");
        }
        
        // Vérification spécifique pour les producteurs
        if (utilisateur.getRole() == Role.PRODUCTEUR) {
            Producteur producteur = producteurRepository.findByTelephone(loginRequest.telephone());
            
            if (producteur.getStatutProducteur() == StatutProducteur.EN_ATTENTE) {
                throw new IllegalStateException("Votre compte est en attente de validation par un administrateur");
            }
            
            if (producteur.getStatutProducteur() == StatutProducteur.REFUSE) {
                throw new IllegalStateException("Votre compte a été refusé. Raison : " + 
                    (producteur.getMotifDeRejet() != null ? producteur.getMotifDeRejet() : "Non spécifiée"));
            }
        }
        
        // Générer le token JWT
        String token = jwtService.generateToken(
            utilisateur.getId(),
            utilisateur.getTelephone(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getRole()
        );
        
        // Retourner la réponse avec le token
        return new AuthResponse(
            token,
            utilisateur.getId(),
            utilisateur.getNom(),
            utilisateur.getPrenom(),
            utilisateur.getEmail(),
            utilisateur.getTelephone(),
            utilisateur.getRole()
        );
    }
    
    /**
     * Connexion spécifique pour Admin
     */
    public AuthResponse loginAdmin(LoginRequest loginRequest) {
        Admin admin = adminRepository.findByTelephone(loginRequest.telephone());
        
        if (admin == null) {
            throw new EntityNotFoundException("Compte administrateur non trouvé");
        }
        
        if (!passwordEncoder.matches(loginRequest.motDePasse(), admin.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        String token = jwtService.generateToken(
            admin.getId(),
            admin.getTelephone(),
            admin.getNom(),
            admin.getPrenom(),
            admin.getRole()
        );
        
        return new AuthResponse(
            token,
            admin.getId(),
            admin.getNom(),
            admin.getPrenom(),
            admin.getEmail(),
            admin.getTelephone(),
            admin.getRole()
        );
    }
    
    /**
     * Connexion spécifique pour Producteur
     */
    public AuthResponse loginProducteur(LoginRequest loginRequest) {
        Producteur producteur = producteurRepository.findByTelephone(loginRequest.telephone());
        
        if (producteur == null) {
            throw new EntityNotFoundException("Compte producteur non trouvé");
        }
        
        if (!passwordEncoder.matches(loginRequest.motDePasse(), producteur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        // Vérifier le statut du producteur
        if (producteur.getStatutProducteur() == StatutProducteur.EN_ATTENTE) {
            throw new IllegalStateException("Votre compte est en attente de validation");
        }
        
        if (producteur.getStatutProducteur() == StatutProducteur.REFUSE) {
            throw new IllegalStateException("Votre compte a été refusé");
        }
        
        String token = jwtService.generateToken(
            producteur.getId(),
            producteur.getTelephone(),
            producteur.getNom(),
            producteur.getPrenom(),
            producteur.getRole()
        );
        
        return new AuthResponse(
            token,
            producteur.getId(),
            producteur.getNom(),
            producteur.getPrenom(),
            producteur.getEmail(),
            producteur.getTelephone(),
            producteur.getRole()
        );
    }
    
    /**
     * Connexion spécifique pour Consommateur
     */
    public AuthResponse loginConsommateur(LoginRequest loginRequest) {
        Consommateur consommateur = consommateurRepository.findByTelephone(loginRequest.telephone());
        
        if (consommateur == null) {
            throw new EntityNotFoundException("Compte consommateur non trouvé");
        }
        
        if (!passwordEncoder.matches(loginRequest.motDePasse(), consommateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        String token = jwtService.generateToken(
            consommateur.getId(),
            consommateur.getTelephone(),
            consommateur.getNom(),
            consommateur.getPrenom(),
            consommateur.getRole()
        );
        
        return new AuthResponse(
            token,
            consommateur.getId(),
            consommateur.getNom(),
            consommateur.getPrenom(),
            consommateur.getEmail(),
            consommateur.getTelephone(),
            consommateur.getRole()
        );
    }
    
    /**
     * Valider un token JWT
     */
    public boolean validateToken(String token, String telephone) {
        return jwtService.isTokenValid(token, telephone);
    }
    
    /**
     * Extraire le téléphone du token
     */
    public String extractTelephone(String token) {
        return jwtService.extractUsername(token);
    }
    
    /**
     * Extraire le rôle du token
     */
    public String extractRole(String token) {
        return jwtService.extractRole(token);
    }
    
    /**
     * Vérifier si l'utilisateur a le rôle requis
     */
    public boolean hasRole(String token, Role requiredRole) {
        String role = jwtService.extractRole(token);
        return role != null && role.equals(requiredRole.name());
    }
}
