package odk.SuguConnect.Security;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Admin;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Classe_abstraite.Utilisateur;
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
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final ConsommateurRepository consommateurRepository;

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
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name());
        return User.builder()
                .username(utilisateur.getTelephone())
                .password(utilisateur.getMotDePasse())
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(!utilisateur.isActif())
                .credentialsExpired(false)
                .disabled(!utilisateur.isActif())
                .build();
    }
}
