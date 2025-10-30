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
import odk.SuguConnect.Repository.AdminRepository;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.UtilisateurRepository;
import odk.SuguConnect.Security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final ConsommateurRepository consommateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthResponse login(LoginRequest loginRequest) {
        // Chercher dans chaque repository spécifique (Admin, Producteur, Consommateur)
        Admin admin = adminRepository.findByTelephone(loginRequest.telephone());
        if (admin != null) {
            return authenticateAdmin(admin, loginRequest.motDePasse());
        }
        
        Producteur producteur = producteurRepository.findByTelephone(loginRequest.telephone());
        if (producteur != null) {
            return authenticateProducteur(producteur, loginRequest.motDePasse());
        }
        
        Consommateur consommateur = consommateurRepository.findByTelephone(loginRequest.telephone());
        if (consommateur != null) {
            return authenticateConsommateur(consommateur, loginRequest.motDePasse());
        }
        throw new EntityNotFoundException("Aucun compte trouvé avec ce numéro de téléphone");
    }

    // Authentifier un admin avec vérifications

    private AuthResponse authenticateAdmin(Admin admin, String motDePasse) {
        if (!passwordEncoder.matches(motDePasse, admin.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        if (!admin.isActif()) {
            throw new IllegalStateException("Votre compte est désactivé. Veuillez contacter l'administrateur.");
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
    
    //Authentifier un producteur avec vérifications de statut
    private AuthResponse authenticateProducteur(Producteur producteur, String motDePasse) {
        if (!passwordEncoder.matches(motDePasse, producteur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        if (!producteur.isActif()) {
            throw new IllegalStateException("Votre compte est désactivé. Veuillez contacter l'administrateur.");
        }
        
        if (producteur.getStatutProducteur() == StatutProducteur.EN_ATTENTE) {
            throw new IllegalStateException("Votre compte est en attente de validation par un administrateur");
        }
        
        if (producteur.getStatutProducteur() == StatutProducteur.REFUSE) {
            throw new IllegalStateException("Votre compte a été refusé. Raison : " + 
                (producteur.getMotifDeRejet() != null ? producteur.getMotifDeRejet() : "Non spécifiée"));
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
    
    //Authentifier un consommateur avec vérifications

    private AuthResponse authenticateConsommateur(Consommateur consommateur, String motDePasse) {
        if (!passwordEncoder.matches(motDePasse, consommateur.getMotDePasse())) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        
        if (!consommateur.isActif()) {
            throw new IllegalStateException("Votre compte est désactivé. Veuillez contacter l'administrateur.");
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
    
    // Connexion spécifique pour Admin
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
    
    // Connexion spécifique pour Producteur
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
    
    //Connexion spécifique pour Consommateur

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
    
    // Valider un token JWT

    public boolean validateToken(String token, String telephone) {
        return jwtService.isTokenValid(token, telephone);
    }
    
    //Extraire le téléphone du token

    public String extractTelephone(String token) {
        return jwtService.extractUsername(token);
    }
    
    // Extraire le rôle du token

    public String extractRole(String token) {
        return jwtService.extractRole(token);
    }
    
    //Vérifier si l'utilisateur a le rôle requis
    public boolean hasRole(String token, Role requiredRole) {
        String role = jwtService.extractRole(token);
        return role != null && role.equals(requiredRole.name());
    }
}
