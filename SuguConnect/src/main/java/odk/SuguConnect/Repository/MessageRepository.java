package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Interface.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") Utilisateur user1, @Param("user2") Utilisateur user2);
    
    List<Message> findByReceiverAndIsReadFalse(Consommateur receiver);
    
    List<Message> findByReceiverAndIsReadFalse(Producteur receiver);
    
    long countByReceiverAndIsReadFalse(Consommateur receiver);
    
    long countByReceiverAndIsReadFalse(Producteur receiver);
}