package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier , Integer> {
    Optional<Panier> findByConsommateur(Consommateur consommateur);
}
