package odk.SuguConnect.Security;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.UtilisateurRepository;
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

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Charger un utilisateur par son téléphone (username)
     * 
     * @param telephone Le numéro de téléphone de l'utilisateur
     * @return UserDetails contenant les informations de l'utilisateur
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        // Rechercher l'utilisateur par téléphone
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec le téléphone: " + telephone
                ));

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
