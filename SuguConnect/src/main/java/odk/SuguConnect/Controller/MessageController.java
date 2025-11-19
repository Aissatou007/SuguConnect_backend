package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(
            summary = "Envoyer un message texte",
            description = "Envoyer un message texte entre deux utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de message invalides")
    })
    public ResponseEntity<Message> sendMessage(
            @Parameter(description = "Données du message", required = true)
            @RequestBody Map<String, Object> messageData) {
        
        try {
            System.out.println("=== Début sendMessage ===");
            System.out.println("Données reçues: " + messageData);
            
            // Validation des données
            if (messageData == null || messageData.isEmpty()) {
                System.out.println("Données de message invalides: données vides");
                return ResponseEntity.badRequest().build();
            }
            
            Object senderIdObj = messageData.get("senderId");
            Object receiverIdObj = messageData.get("receiverId");
            Object contentObj = messageData.get("content");
            Object typeObj = messageData.get("type");
            
            if (senderIdObj == null || receiverIdObj == null || contentObj == null) {
                System.out.println("Données de message invalides: champs manquants");
                System.out.println("senderId: " + senderIdObj);
                System.out.println("receiverId: " + receiverIdObj);
                System.out.println("content: " + contentObj);
                return ResponseEntity.badRequest().build();
            }
            
            int senderId = (int) senderIdObj;
            int receiverId = (int) receiverIdObj;
            String content = (String) contentObj;
            String type = (String) (typeObj != null ? typeObj : "TEXT");
            
            System.out.println("senderId: " + senderId);
            System.out.println("receiverId: " + receiverId);
            System.out.println("content: " + content);
            System.out.println("type: " + type);
            
            Message message = messageService.sendMessage(senderId, receiverId, content, type);
            System.out.println("=== Fin sendMessage ===");
            return ResponseEntity.status(201).body(message);
        } catch (Exception e) {
            System.out.println("=== ERREUR sendMessage ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR sendMessage ===");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/voice")
    @Operation(
            summary = "Envoyer un message vocal",
            description = "Envoyer un message vocal entre deux utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message vocal envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'envoi du message vocal")
    })
    public ResponseEntity<Message> sendVoiceMessage(
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam int senderId,
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam int receiverId,
            @Parameter(description = "Fichier audio", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            Message message = messageService.sendVoiceMessage(senderId, receiverId, file);
            return ResponseEntity.status(201).body(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/image")
    @Operation(
            summary = "Envoyer une image",
            description = "Envoyer une image entre deux utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image envoyée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'envoi de l'image")
    })
    public ResponseEntity<Message> sendImage(
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam int senderId,
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam int receiverId,
            @Parameter(description = "Fichier image", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            Message message = messageService.sendImage(senderId, receiverId, file);
            return ResponseEntity.status(201).body(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/file")
    @Operation(
            summary = "Envoyer un fichier",
            description = "Envoyer un fichier entre deux utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fichier envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'envoi du fichier")
    })
    public ResponseEntity<Message> sendFile(
            @Parameter(description = "ID de l'expéditeur", required = true)
            @RequestParam int senderId,
            @Parameter(description = "ID du destinataire", required = true)
            @RequestParam int receiverId,
            @Parameter(description = "Fichier", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            Message message = messageService.sendFile(senderId, receiverId, file);
            return ResponseEntity.status(201).body(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conversation")
    @Operation(
            summary = "Récupérer l'historique de conversation",
            description = "Récupérer tous les messages entre deux utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    public ResponseEntity<List<Message>> getConversation(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam int userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam int userId2) {
        
        try {
            System.out.println("=== Début getConversation ===");
            System.out.println("userId1: " + userId1);
            System.out.println("userId2: " + userId2);
            
            // Validation des paramètres
            if (userId1 <= 0 || userId2 <= 0) {
                System.out.println("Paramètres invalides: userId1=" + userId1 + ", userId2=" + userId2);
                return ResponseEntity.badRequest().build();
            }
            
            List<Message> messages = messageService.getConversation(userId1, userId2);
            System.out.println("Messages trouvés: " + messages.size());
            System.out.println("=== Fin getConversation ===");
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            System.out.println("=== ERREUR getConversation ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR getConversation ===");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/read")
    @Operation(
            summary = "Marquer un message comme lu",
            description = "Marquer un message spécifique comme lu"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marqué comme lu"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé")
    })
    public ResponseEntity<Message> markAsRead(
            @Parameter(description = "ID du message", required = true)
            @PathVariable int id) {
        
        try {
            Message message = messageService.markAsRead(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/unread/{userId}")
    @Operation(
            summary = "Compter les messages non lus",
            description = "Compter le nombre de messages non lus pour un utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comptage effectué avec succès")
    })
    public ResponseEntity<Long> countUnreadMessages(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable int userId) {
        
        long count = messageService.countUnreadMessages(userId);
        return ResponseEntity.ok(count);
    }
}