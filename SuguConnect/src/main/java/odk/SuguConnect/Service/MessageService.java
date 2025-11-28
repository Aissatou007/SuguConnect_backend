package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.MessageRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Service.NotificationService;
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
    private final NotificationService notificationService;
    private final Path fileStorageLocation = Paths.get("uploads/messages").toAbsolutePath().normalize();

    @Transactional
    public Message sendMessage(int senderId, int receiverId, String content, String type) {
        try {
            System.out.println("=== Début sendMessage ===");
            System.out.println("senderId: " + senderId);
            System.out.println("receiverId: " + receiverId);
            System.out.println("content: " + content);
            System.out.println("type: " + type);
            
            // Vérifier que les utilisateurs existent
            Utilisateur sender = findUserById(senderId);
            Utilisateur receiver = findUserById(receiverId);
            System.out.println("Expéditeur trouvé: " + sender.getClass().getSimpleName());
            System.out.println("Destinataire trouvé: " + receiver.getClass().getSimpleName());
            
            // Valider le type de message
            Message.MessageType messageType;
            try {
                messageType = Message.MessageType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Type de message invalide, utilisation de TEXT par défaut");
                messageType = Message.MessageType.TEXT;
            }
            System.out.println("Type de message: " + messageType);
            
            // Créer le message
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(content);
            message.setType(messageType);
            message.setTimestamp(LocalDateTime.now());
            // Forcer explicitement isRead à false pour garantir qu'il est inclus dans l'INSERT
            message.setRead(false);
            
            // Vérification avant sauvegarde
            System.out.println("Message avant sauvegarde - isRead: " + message.isRead());
            
            Message savedMessage = messageRepository.save(message);
            System.out.println("Message après sauvegarde - isRead: " + savedMessage.isRead());
            System.out.println("Message sauvegardé avec ID: " + savedMessage.getIdMessage());
            
            // Envoyer une notification au destinataire dans une transaction séparée (pour ne pas bloquer l'envoi du message)
            // Utiliser INFO_SYSTEME car NOUVEAU_MESSAGE n'existe pas encore dans l'ENUM de la base de données
            try {
                String senderName = sender.getNom() + " " + sender.getPrenom();
                String notificationMessage = senderName + " vous a envoyé un message: " + 
                    (content.length() > 50 ? content.substring(0, 50) + "..." : content);
                String action = "/chat/" + senderId; // Action pour ouvrir le chat
                
                // Utiliser creerNotificationAsync pour isoler la transaction
                notificationService.creerNotificationAsync(
                    receiverId, 
                    TypeMessage.INFO_SYSTEME, 
                    notificationMessage, 
                    action,
                    168 // 7 jours
                );
                System.out.println("Notification envoyée au destinataire ID: " + receiverId);
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de l'envoi de la notification: " + e.getMessage());
                System.out.println("⚠️ Type d'erreur: " + e.getClass().getSimpleName());
                e.printStackTrace();
                // Ne pas faire échouer l'envoi du message si la notification échoue
                // L'erreur est loggée mais n'interrompt pas la transaction du message
            }
            
            System.out.println("=== Fin sendMessage ===");
            return savedMessage;
        } catch (Exception e) {
            System.out.println("=== ERREUR sendMessage ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR sendMessage ===");
            throw e;
        }
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
        try {
            System.out.println("=== Début getConversation (service) ===");
            System.out.println("Recherche utilisateur 1 (ID: " + userId1 + ")");
            Utilisateur user1 = findUserById(userId1);
            System.out.println("Utilisateur 1 trouvé: " + user1.getClass().getSimpleName() + " - " + user1.getId());
            
            System.out.println("Recherche utilisateur 2 (ID: " + userId2 + ")");
            Utilisateur user2 = findUserById(userId2);
            System.out.println("Utilisateur 2 trouvé: " + user2.getClass().getSimpleName() + " - " + user2.getId());
            
            System.out.println("Recherche conversation dans le repository...");
            List<Message> messages = messageRepository.findConversationBetweenUsers(user1, user2);
            System.out.println("Conversation trouvée avec " + messages.size() + " messages");
            System.out.println("=== Fin getConversation (service) ===");
            return messages;
        } catch (Exception e) {
            System.out.println("=== ERREUR getConversation (service) ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR getConversation (service) ===");
            throw e;
        }
    }

    @Transactional
    public Message markAsRead(int messageId) {
        Message message = findMessageById(messageId);
        message.setRead(true);
        return messageRepository.save(message);
    }

    public boolean userExists(int id) {
        try {
            Utilisateur user = findUserById(id);
            return user != null;
        } catch (Exception e) {
            return false;
        }
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
        System.out.println("=== Recherche utilisateur ID: " + id + " ===");
        
        // Vérifier d'abord dans le repository des consommateurs
        try {
            Consommateur consommateur = consommateurRepository.findById(id).orElse(null);
            if (consommateur != null) {
                System.out.println("✅ Consommateur trouvé avec ID: " + id + ", Nom: " + consommateur.getNom() + " " + consommateur.getPrenom());
                return consommateur;
            }
            System.out.println("❌ Consommateur non trouvé avec ID: " + id);
        } catch (Exception e) {
            System.out.println("⚠️ Erreur lors de la recherche du consommateur ID " + id + ": " + e.getMessage());
        }
        
        // Ensuite dans le repository des producteurs
        try {
            Producteur producteur = producteurRepository.findById(id).orElse(null);
            if (producteur != null) {
                System.out.println("✅ Producteur trouvé avec ID: " + id + ", Nom: " + producteur.getNom() + " " + producteur.getPrenom());
                return producteur;
            }
            System.out.println("❌ Producteur non trouvé avec ID: " + id);
        } catch (Exception e) {
            System.out.println("⚠️ Erreur lors de la recherche du producteur ID " + id + ": " + e.getMessage());
        }
        
        // Vérifier si l'ID existe dans la table utilisateur (peut-être un autre type d'utilisateur)
        System.out.println("❌ Aucun utilisateur trouvé (ni consommateur ni producteur) avec ID: " + id);
        throw new EntityNotFoundException("Utilisateur introuvable avec ID: " + id + ". Vérifiez que l'utilisateur existe dans la base de données (consommateur ou producteur).");
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