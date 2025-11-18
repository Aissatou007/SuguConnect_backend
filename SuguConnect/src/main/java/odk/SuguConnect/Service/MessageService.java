package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.MessageRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConsommateurRepository consommateurRepository;
    private final ProducteurRepository producteurRepository;
    private final Path fileStorageLocation = Paths.get("uploads/messages").toAbsolutePath().normalize();

    @Transactional
    public Message sendMessage(int senderId, int receiverId, String content, String type) {
        // Vérifier que les utilisateurs existent
        Utilisateur sender = findUserById(senderId);
        Utilisateur receiver = findUserById(receiverId);
        
        // Créer le message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.valueOf(type));
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        
        return messageRepository.save(message);
    }

    @Transactional
    public Message sendVoiceMessage(int senderId, int receiverId, MultipartFile file) {
        // Vérifier que les utilisateurs existent
        Utilisateur sender = findUserById(senderId);
        Utilisateur receiver = findUserById(receiverId);
        
        // Sauvegarder le fichier
        String fileName = saveFile(file, "voice");
        
        // Créer le message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Message vocal");
        message.setType(Message.MessageType.VOICE);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setFilePath("messages/voice/" + fileName);
        
        return messageRepository.save(message);
    }

    @Transactional
    public Message sendImage(int senderId, int receiverId, MultipartFile file) {
        // Vérifier que les utilisateurs existent
        Utilisateur sender = findUserById(senderId);
        Utilisateur receiver = findUserById(receiverId);
        
        // Sauvegarder le fichier
        String fileName = saveFile(file, "images");
        
        // Créer le message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Image");
        message.setType(Message.MessageType.IMAGE);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setFilePath("messages/images/" + fileName);
        
        return messageRepository.save(message);
    }

    @Transactional
    public Message sendFile(int senderId, int receiverId, MultipartFile file) {
        // Vérifier que les utilisateurs existent
        Utilisateur sender = findUserById(senderId);
        Utilisateur receiver = findUserById(receiverId);
        
        // Sauvegarder le fichier
        String fileName = saveFile(file, "files");
        
        // Créer le message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent("Fichier");
        message.setType(Message.MessageType.FILE);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setFilePath("messages/files/" + fileName);
        
        return messageRepository.save(message);
    }

    public List<Message> getConversation(int userId1, int userId2) {
        Utilisateur user1 = findUserById(userId1);
        Utilisateur user2 = findUserById(userId2);
        
        return messageRepository.findConversationBetweenUsers(user1, user2);
    }

    @Transactional
    public Message markAsRead(int messageId) {
        Message message = findMessageById(messageId);
        message.setRead(true);
        return messageRepository.save(message);
    }

    public long countUnreadMessages(int userId) {
        // Pour cette méthode, nous devons trouver l'utilisateur par son ID
        // Nous allons vérifier dans les deux repositories
        Consommateur consommateur = consommateurRepository.findById(userId).orElse(null);
        if (consommateur != null) {
            return messageRepository.countByReceiverAndIsReadFalse(consommateur);
        }
        
        Producteur producteur = producteurRepository.findById(userId).orElse(null);
        if (producteur != null) {
            return messageRepository.countByReceiverAndIsReadFalse(producteur);
        }
        
        return 0;
    }

    // ========== Méthodes privées utilitaires ==========

    private Utilisateur findUserById(int id) {
        // Vérifier d'abord dans le repository des consommateurs
        Consommateur consommateur = consommateurRepository.findById(id).orElse(null);
        if (consommateur != null) {
            return consommateur;
        }
        
        // Ensuite dans le repository des producteurs
        Producteur producteur = producteurRepository.findById(id).orElse(null);
        if (producteur != null) {
            return producteur;
        }
        
        throw new EntityNotFoundException("Utilisateur introuvable avec ID: " + id);
    }

    private Message findMessageById(int id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message introuvable avec ID: " + id));
    }

    private String saveFile(MultipartFile file, String subfolder) {
        try {
            // Créer le répertoire si nécessaire
            Path targetLocation = fileStorageLocation.resolve(subfolder);
            Files.createDirectories(targetLocation);

            // Générer un nom de fichier unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Sauvegarder le fichier
            Path filePath = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }
}