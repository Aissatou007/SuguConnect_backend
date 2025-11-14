package odk.SuguConnect.Repository;

import odk.SuguConnect.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByDateEnvoiAsc(Long conversationId);
    List<Message> findByExpediteurIdAndDestinataireIdOrderByDateEnvoiAsc(Long expediteurId, Long destinataireId);
}