package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.DTO.ProduitPopulaireDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    List<Produit> findByProducteur(Producteur producteur);
    List<Produit> findAllByStockDisponibleGreaterThan(int stock);
    List<Produit> findByCategorieId(int id);
    List<Produit> findByNomContainingIgnoreCase(String nom);
    List<Produit> findByCategorieAndStockDisponibleGreaterThan(Categorie categorie, int stock);
    
    // Charger le produit avec son producteur et sa catégorie
    @EntityGraph(attributePaths = {"producteur", "categorie"})
    @Query("SELECT p FROM Produit p WHERE p.id = :id")
    Optional<Produit> findByIdWithProducteurAndCategorie(@Param("id") int id);
    
    @Query("SELECT new odk.SuguConnect.DTO.ProduitPopulaireDTO(" +
           "p.id, p.nom, p.description, p.prixUnitaire, " +
           "p.unite, " +  // Utilisation directe de l'enum unite
           "'' , " +  // On met une chaîne vide pour la photo, on la gérera dans le service
           "SUM(cp.quantite), " +
           "pr.id, pr.nom, " +
           "c.id, c.libelle) " +
           "FROM Produit p " +
           "JOIN p.commandeProduitList cp " +
           "JOIN p.producteur pr " +
           "JOIN p.categorie c " +
           "GROUP BY p.id, p.nom, p.description, p.prixUnitaire, p.unite, pr.id, pr.nom, c.id, c.libelle " +
           "ORDER BY SUM(cp.quantite) DESC")
    List<ProduitPopulaireDTO> findTopProduitsPopulaires();
    
    @Query(value = "SELECT new odk.SuguConnect.DTO.ProduitPopulaireDTO(" +
           "p.id, p.nom, p.description, p.prixUnitaire, " +
           "p.unite, " +  // Utilisation directe de l'enum unite
           "'' , " +  // On met une chaîne vide pour la photo, on la gérera dans le service
           "SUM(cp.quantite), " +
           "pr.id, pr.nom, " +
           "c.id, c.libelle) " +
           "FROM Produit p " +
           "JOIN p.commandeProduitList cp " +
           "JOIN p.producteur pr " +
           "JOIN p.categorie c " +
           "GROUP BY p.id, p.nom, p.description, p.prixUnitaire, p.unite, pr.id, pr.nom, c.id, c.libelle " +
           "ORDER BY SUM(cp.quantite) DESC")
    List<ProduitPopulaireDTO> findTopProduitsPopulairesLimit(@Param("limit") int limit);
}