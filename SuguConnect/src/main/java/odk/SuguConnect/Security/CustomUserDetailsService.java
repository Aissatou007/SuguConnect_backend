package odk.SuguConnect.Security;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.AdminRepository;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service personnalisé pour charger les détails de l'utilisateur
 * Utilisé par Spring Security pour l'authentification
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final ConsommateurRepository consommateurRepository;

    /**
     * Charger un utilisateur par son téléphone (username)
     * Recherche dans tous les repositories pour supporter l'héritage JOINED
     * 
     * @param telephone Le numéro de téléphone de l'utilisateur
     * @return UserDetails contenant les informations de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        // Rechercher dans chaque repository spécifique
        Utilisateur utilisateur = null;
        
        // Chercher dans Admin
        Admin admin = adminRepository.findByTelephone(telephone);
        if (admin != null) {
            utilisateur = admin;
        }
        
        // Chercher dans Producteur si non trouvé
        if (utilisateur == null) {
            Producteur producteur = producteurRepository.findByTelephone(telephone);
            if (producteur != null) {
                utilisateur = producteur;
            }
        }
        
        // Chercher dans Consommateur si non trouvé
        if (utilisateur == null) {
            Consommateur consommateur = consommateurRepository.findByTelephone(telephone);
            if (consommateur != null) {
                utilisateur = consommateur;
            }
        }
        
        // Si aucun utilisateur trouvé, lancer une exception
        if (utilisateur == null) {
            throw new UsernameNotFoundException(
                "Utilisateur non trouvé avec le téléphone: " + telephone
            );
        }

        // Créer l'autorité (rôle) avec le préfixe ROLE_
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name());

        // Retourner un objet UserDetails de Spring Security
        return User.builder()
                .username(utilisateur.getTelephone())
                .password(utilisateur.getMotDePasse())
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(!utilisateur.isActif()) // Compte verrouillé si inactif
                .credentialsExpired(false)
                .disabled(!utilisateur.isActif()) // Compte désactivé si inactif
                .build();
    }
}
