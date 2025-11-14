package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByConsommateurId(Long consommateurId);
    List<Conversation> findByProducteurId(Long producteurId);
    Optional<Conversation> findByConsommateurIdAndProducteurIdAndProduitId(Long consommateurId, Long producteurId, Long produitId);
    List<Conversation> findByConsommateurIdAndActiveTrue(Long consommateurId);
    List<Conversation> findByProducteurIdAndActiveTrue(Long producteurId);
}