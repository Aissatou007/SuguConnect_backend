package odk.SuguConnect.Repository;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.StatutProducteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProducteurRepository extends JpaRepository<Producteur , Integer> {
    Producteur findByTelephone(String telephone);
    List<Producteur> findByStatutProducteur(StatutProducteur statut);

}
