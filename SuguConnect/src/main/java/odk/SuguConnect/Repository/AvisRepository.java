package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Avis;
import odk.SuguConnect.Entity.Producteur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Integer> {
    List<Avis> findByProducteurAndValideTrue(Producteur producteur);
    List<Avis> findByCommandeId(int commandeId);
    boolean existsByCommandeId(int commandeId);
}
