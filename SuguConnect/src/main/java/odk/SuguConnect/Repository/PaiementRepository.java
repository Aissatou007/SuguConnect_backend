package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement , Integer> {
    List<Paiement> findByStatutPaiement(StatutPaiement statutPaiement);
    List<Paiement> findByConsommateur(Consommateur consommateur);
}
