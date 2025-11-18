package odk.SuguConnect.Service;

import odk.SuguConnect.Classe_abstraite.Utilisateur;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurService {
    
    @Autowired
    private ConsommateurRepository consommateurRepository;
    
    @Autowired
    private ProducteurRepository producteurRepository;
    
    public Optional<Consommateur> getConsommateurById(int id) {
        return consommateurRepository.findById(id);
    }
    
    public Optional<Producteur> getProducteurById(int id) {
        return producteurRepository.findById(id);
    }
    
    public Optional<? extends Utilisateur> getUtilisateurById(int id) {
        // Essayer de trouver un consommateur
        Optional<Consommateur> consommateur = getConsommateurById(id);
        if (consommateur.isPresent()) {
            return consommateur;
        }
        
        // Si pas trouvé, essayer de trouver un producteur
        Optional<Producteur> producteur = getProducteurById(id);
        if (producteur.isPresent()) {
            return producteur;
        }
        
        return Optional.empty();
    }
}