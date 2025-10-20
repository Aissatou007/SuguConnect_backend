package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement , Integer> {
    Optional<Paiement> findByTransactionId(String transactionId);
    List<Paiement> findByStatus(StatutPaiement status);
}
