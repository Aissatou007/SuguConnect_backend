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
    
    @Query("SELECT c FROM Commande c LEFT JOIN FETCH c.commandeProduits cp LEFT JOIN FETCH cp.produit p LEFT JOIN FETCH p.producteur WHERE c.consommateur = :consommateur")
    List<Commande> findByConsommateurWithRelations(@Param("consommateur") Consommateur consommateur);
    
    @Query("SELECT DISTINCT c FROM Commande c " +
           "LEFT JOIN FETCH c.commandeProduits cp " +
           "LEFT JOIN FETCH cp.produit p " +
           "LEFT JOIN FETCH p.producteur pr " +
           "LEFT JOIN FETCH p.categorie " +
           "LEFT JOIN FETCH c.consommateur " +
           "LEFT JOIN FETCH c.paiement " +
           "WHERE pr.id = :producteurId")
    List<Commande> findByProducteurWithRelations(@Param("producteurId") int producteurId);
    
    List<Commande> findByStatutCommande(StatutCommande statut);
    int countByConsommateurId(Long consommateurId);
}
