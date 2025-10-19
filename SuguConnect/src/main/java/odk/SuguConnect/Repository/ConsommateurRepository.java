package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsommateurRepository extends JpaRepository<Consommateur , Integer> {
    // Requête customiser pour pouvoir recuprer le user via son numero de télephone
    Consommateur findByTelephone(String telephone);
    
}
