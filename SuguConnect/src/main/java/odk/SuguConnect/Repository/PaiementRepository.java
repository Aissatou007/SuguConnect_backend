package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement , Integer> {
}
