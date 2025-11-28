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

import java.util.HashMap;
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
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "Données du message", required = true)
            @RequestBody Map<String, Object> messageData) {
        
        int senderId = 0;
        int receiverId = 0;
        
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
            
            // Conversion sécurisée des IDs (gère Integer, Number, etc.)
            try {
                if (senderIdObj instanceof Number) {
                    senderId = ((Number) senderIdObj).intValue();
                } else if (senderIdObj instanceof Integer) {
                    senderId = (Integer) senderIdObj;
                } else {
                    senderId = Integer.parseInt(senderIdObj.toString());
                }
                
                if (receiverIdObj instanceof Number) {
                    receiverId = ((Number) receiverIdObj).intValue();
                } else if (receiverIdObj instanceof Integer) {
                    receiverId = (Integer) receiverIdObj;
                } else {
                    receiverId = Integer.parseInt(receiverIdObj.toString());
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur de conversion des IDs: " + e.getMessage());
                System.out.println("senderId type: " + senderIdObj.getClass().getName() + ", value: " + senderIdObj);
                System.out.println("receiverId type: " + receiverIdObj.getClass().getName() + ", value: " + receiverIdObj);
                return ResponseEntity.badRequest().build();
            }
            
            String content = contentObj.toString();
            String type = (typeObj != null ? typeObj.toString() : "TEXT");
            
            System.out.println("senderId: " + senderId);
            System.out.println("receiverId: " + receiverId);
            System.out.println("content: " + content);
            System.out.println("type: " + type);
            
            Message message = messageService.sendMessage(senderId, receiverId, content, type);
            System.out.println("=== Fin sendMessage ===");
            return ResponseEntity.status(201).body(message);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            System.out.println("=== ERREUR sendMessage (EntityNotFound) ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR sendMessage ===");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "L'utilisateur spécifié n'existe pas dans la base de données");
            errorResponse.put("details", "Vérifiez que les IDs " + senderId + " et " + receiverId + " existent dans la base de données");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.out.println("=== ERREUR sendMessage ===");
            System.out.println("Erreur: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR sendMessage ===");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de l'envoi du message: " + e.getMessage());
            errorResponse.put("type", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                errorResponse.put("cause", e.getCause().getMessage());
            }
            return ResponseEntity.badRequest().body(errorResponse);
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
    public ResponseEntity<?> getConversation(
            @Parameter(description = "ID du premier utilisateur", required = true)
            @RequestParam(required = false) String userId1,
            @Parameter(description = "ID du second utilisateur", required = true)
            @RequestParam(required = false) String userId2) {
        
        try {
            System.out.println("=== Début getConversation ===");
            System.out.println("userId1 (String): " + userId1);
            System.out.println("userId2 (String): " + userId2);
            
            // Validation des paramètres
            if (userId1 == null || userId2 == null || userId1.isEmpty() || userId2.isEmpty()) {
                System.out.println("Paramètres manquants: userId1=" + userId1 + ", userId2=" + userId2);
                return ResponseEntity.badRequest().body(Map.of("error", "Les paramètres userId1 et userId2 sont requis"));
            }
            
            // Conversion en int
            int userId1Int;
            int userId2Int;
            try {
                userId1Int = Integer.parseInt(userId1);
                userId2Int = Integer.parseInt(userId2);
            } catch (NumberFormatException e) {
                System.out.println("Erreur de conversion: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", "Les IDs doivent être des nombres valides"));
            }
            
            System.out.println("userId1 (int): " + userId1Int);
            System.out.println("userId2 (int): " + userId2Int);
            
            // Validation des valeurs
            if (userId1Int <= 0 || userId2Int <= 0) {
                System.out.println("Paramètres invalides: userId1=" + userId1Int + ", userId2=" + userId2Int);
                return ResponseEntity.badRequest().body(Map.of("error", "Les IDs doivent être positifs"));
            }
            
            List<Message> messages = messageService.getConversation(userId1Int, userId2Int);
            System.out.println("Messages trouvés: " + messages.size());
            System.out.println("=== Fin getConversation ===");
            return ResponseEntity.ok(messages);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            System.out.println("=== ERREUR getConversation (EntityNotFound) ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR getConversation ===");
            return ResponseEntity.badRequest().body(Map.of("error", "Utilisateur introuvable: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("=== ERREUR getConversation ===");
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== FIN ERREUR getConversation ===");
            return ResponseEntity.badRequest().body(Map.of("error", "Erreur lors de la récupération de la conversation: " + e.getMessage()));
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