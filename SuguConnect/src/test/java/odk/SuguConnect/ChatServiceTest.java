package odk.SuguConnect;

import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Service.MessageService;
import odk.SuguConnect.Service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChatServiceTest {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private ConversationService conversationService;
    
    @Test
    public void testCreerConversation() {
        // Test de création d'une conversation
        Conversation conversation = conversationService.creerConversation(1L, 1L, 1L);
        assertNotNull(conversation);
        assertEquals(1L, conversation.getConsommateur().getId());
        assertEquals(1L, conversation.getProducteur().getId());
    }
    
    @Test
    public void testEnvoyerMessageTexte() {
        // Test d'envoi d'un message texte
        Message message = messageService.creerMessage(1L, 1L, 1L, "Bonjour, je suis intéressé par votre produit", TypeMessage.TEXTE, null);
        assertNotNull(message);
        assertEquals("Bonjour, je suis intéressé par votre produit", message.getContenu());
        assertEquals(TypeMessage.TEXTE, message.getTypeMessage());
    }
    
    @Test
    public void testEnvoyerMessageImage() {
        // Test d'envoi d'un message avec image
        Message message = messageService.creerMessage(1L, 1L, 1L, "Voici une image de mon produit", TypeMessage.IMAGE, "/uploads/image.jpg");
        assertNotNull(message);
        assertEquals("Voici une image de mon produit", message.getContenu());
        assertEquals(TypeMessage.IMAGE, message.getTypeMessage());
        assertEquals("/uploads/image.jpg", message.getCheminFichier());
    }
}