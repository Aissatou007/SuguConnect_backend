package odk.SuguConnect;

import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Service.MessageService;
import odk.SuguConnect.Service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Message message = messageService.sendMessage(1, 2, "Bonjour, je suis intéressé par votre produit", "TEXT");
        assertNotNull(message);
        assertEquals("Bonjour, je suis intéressé par votre produit", message.getContent());
        assertEquals(odk.SuguConnect.Entity.Message.MessageType.TEXT, message.getType());
    }
    
    @Test
    public void testEnvoyerMessageImage() {
        // Créer un fichier mock pour le test
        MockMultipartFile mockFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "Contenu de test".getBytes()
        );
        
        // Test d'envoi d'un message avec image
        Message message = messageService.sendImage(1, 2, mockFile);
        assertNotNull(message);
        assertEquals("Image", message.getContent());
        assertEquals(odk.SuguConnect.Entity.Message.MessageType.IMAGE, message.getType());
        assertNotNull(message.getFilePath());
    }
}