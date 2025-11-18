package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande , Integer> {
    List<Commande> findByConsommateur(Consommateur consommateur);
    List<Commande> findByStatutCommande(StatutCommande statut);
    
    @Query("SELECT DISTINCT c FROM Commande c JOIN c.commandeProduits cp WHERE cp.produit.producteur.id = :producteurId")
    List<Commande> findByProducteurId(@Param("producteurId") int producteurId);
    
    @Query("SELECT DISTINCT c FROM Commande c JOIN c.commandeProduits cp WHERE cp.produit.producteur.id = :producteurId AND c.statutCommande = :statut")
    List<Commande> findByProducteurIdAndStatut(@Param("producteurId") int producteurId, @Param("statut") StatutCommande statut);
}
