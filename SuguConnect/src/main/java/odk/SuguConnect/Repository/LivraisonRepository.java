package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
}