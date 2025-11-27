package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.PanierProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanierProduitRepository extends JpaRepository<PanierProduit, Integer> {
    @Query("SELECT pp FROM PanierProduit pp JOIN FETCH pp.produit p LEFT JOIN FETCH p.producteur WHERE pp.panier.id = :panierId")
    List<PanierProduit> findByPanierId(@Param("panierId") int panierId);
}
