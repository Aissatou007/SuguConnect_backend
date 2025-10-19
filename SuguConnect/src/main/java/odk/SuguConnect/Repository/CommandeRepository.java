package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande , Integer> {
}
