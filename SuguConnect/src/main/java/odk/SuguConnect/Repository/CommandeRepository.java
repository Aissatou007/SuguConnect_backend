package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande , Integer> {
    List<Commande> findByConsommateur(Consommateur consommateur);
    List<Commande> findByStatutCommande(StatutCommande statut);
}
