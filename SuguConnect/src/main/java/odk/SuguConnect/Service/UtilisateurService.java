package odk.SuguConnect.Service;

import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository ;
    private final PasswordEncoder passwordEncoder ;

    public UtilisateurService(UtilisateurRepository utilisateurRepository , PasswordEncoder passwordEncoder) {
        this.utilisateurRepository= utilisateurRepository;
        this.passwordEncoder = passwordEncoder ;
    }
}
