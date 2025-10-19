package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    List<Produit> findByProducteur(Producteur producteur);
    List<Produit> findAllByStockDisponibleGreaterThan(int stock);
}
