package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.PanierProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanierProduitRepository extends JpaRepository<PanierProduit, Integer> {
}
