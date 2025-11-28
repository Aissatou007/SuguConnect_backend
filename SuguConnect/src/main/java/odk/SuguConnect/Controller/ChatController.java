package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import odk.SuguConnect.DTO.ConversationDTO;
import odk.SuguConnect.DTO.MessageDTO;
import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Mapper.ConversationMapper;
import odk.SuguConnect.Mapper.MessageMapper;
import odk.SuguConnect.Service.ConversationService;
import odk.SuguConnect.Service.FileStorageService;
import odk.SuguConnect.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@Tag(name = "Chat", description = "API de messagerie entre consommateurs et producteurs")
@Slf4j
public class ChatController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Operation(
        summary = "Créer ou récupérer une conversation",
        description = "Crée une nouvelle conversation entre un consommateur et un producteur, ou retourne une conversation existante pour le même produit."
    )
    @ApiResponse(responseCode = "200", description = "Conversation créée ou récupérée avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ConversationDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requête invalide")
    @PostMapping("/conversation")
    public ResponseEntity<ConversationDTO> creerConversation(
            @Parameter(description = "ID du consommateur") @RequestParam Long consommateurId,
            @Parameter(description = "ID du producteur") @RequestParam Long producteurId,
            @Parameter(description = "ID du produit (optionnel)") @RequestParam(required = false) Long produitId) {
        
        Conversation conversation = conversationService.creerConversation(consommateurId, producteurId, produitId);
        if (conversation != null) {
            return ResponseEntity.ok(conversationMapper.toDTO(conversation));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(
        summary = "Envoyer un message texte",
        description = "Envoie un message texte entre deux utilisateurs."
    )
    @ApiResponse(responseCode = "200", description = "Message envoyé avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageDTO.class)))
    @PostMapping("/message/texte")
    public ResponseEntity<MessageDTO> envoyerMessageTexte(
            @Parameter(description = "ID de l'expéditeur") @RequestParam Long expediteurId,
            @Parameter(description = "ID du destinataire") @RequestParam Long destinataireId,
            @Parameter(description = "Contenu du message") @RequestParam String contenu) {
        
        Message message = messageService.sendMessage(
                expediteurId.intValue(), destinataireId.intValue(), contenu, "TEXT");
        
        return ResponseEntity.ok(messageMapper.toDTO(message));
    }
    
    @Operation(
        summary = "Uploader un fichier pour le chat",
        description = "Télécharge un fichier (image, vocal, document) pour l'utiliser dans le chat."
    )
    @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès", 
        content = @Content(mediaType = "application/json"))
    @PostMapping("/upload/fichier")
    public ResponseEntity<Map<String, String>> uploadFichierChat(
            @Parameter(description = "Fichier à télécharger") @RequestParam("file") MultipartFile file) {
        
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileDownloadUri", fileDownloadUri);
        response.put("fileType", file.getContentType());
        response.put("size", String.valueOf(file.getSize()));

        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Télécharger un fichier de chat",
        description = "Télécharge un fichier précédemment uploadé dans le chat."
    )
    @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès")
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> telechargerFichierChat(
            @Parameter(description = "Nom du fichier") @PathVariable String fileName,
            HttpServletRequest request) {
        
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @Operation(
        summary = "Envoyer un message avec fichier",
        description = "Envoie un message avec fichier (image, vocal, document) entre deux utilisateurs."
    )
    @ApiResponse(responseCode = "200", description = "Message avec fichier envoyé avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageDTO.class)))
    @PostMapping("/message/fichier")
    public ResponseEntity<MessageDTO> envoyerMessageFichier(
            @Parameter(description = "ID de l'expéditeur") @RequestParam Long expediteurId,
            @Parameter(description = "ID du destinataire") @RequestParam Long destinataireId,
            @Parameter(description = "Contenu du message") @RequestParam String contenu,
            @Parameter(description = "Type du message (TEXTE, IMAGE, VOCAL, DOCUMENT)") @RequestParam TypeMessage typeMessage,
            @Parameter(description = "Fichier") @RequestParam("file") MultipartFile file) {
        
        try {
            Message message;
            switch (typeMessage) {
                case IMAGE:
                    message = messageService.sendImage(expediteurId.intValue(), destinataireId.intValue(), file);
                    break;
                case VOCAL:
                    message = messageService.sendVoiceMessage(expediteurId.intValue(), destinataireId.intValue(), file);
                    break;
                case DOCUMENT:
                    message = messageService.sendFile(expediteurId.intValue(), destinataireId.intValue(), file);
                    break;
                default:
                    message = messageService.sendMessage(expediteurId.intValue(), destinataireId.intValue(), contenu, "TEXT");
                    break;
            }
            
            return ResponseEntity.ok(messageMapper.toDTO(message));
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du message avec fichier", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(
        summary = "Récupérer les messages d'une conversation",
        description = "Récupère tous les messages entre deux utilisateurs, triés par date d'envoi."
    )
    @ApiResponse(responseCode = "200", description = "Messages récupérés avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageDTO.class)))
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(
            @Parameter(description = "ID du premier utilisateur") @RequestParam Long userId1,
            @Parameter(description = "ID du second utilisateur") @RequestParam Long userId2) {
        List<Message> messages = messageService.getConversation(userId1.intValue(), userId2.intValue());
        List<MessageDTO> messageDTOs = messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }
    
    @Operation(
        summary = "Récupérer les conversations d'un consommateur",
        description = "Récupère toutes les conversations actives d'un consommateur."
    )
    @ApiResponse(responseCode = "200", description = "Conversations récupérées avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ConversationDTO.class)))
    @GetMapping("/conversations/consommateur/{consommateurId}")
    public ResponseEntity<List<ConversationDTO>> getConversationsConsommateur(
            @Parameter(description = "ID du consommateur") @PathVariable Long consommateurId) {
        List<Conversation> conversations = conversationService.getConversationsByConsommateur(consommateurId);
        List<ConversationDTO> conversationDTOs = conversations.stream()
                .map(conversationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conversationDTOs);
    }
    
    @Operation(
        summary = "Récupérer les conversations d'un producteur",
        description = "Récupère toutes les conversations actives d'un producteur."
    )
    @ApiResponse(responseCode = "200", description = "Conversations récupérées avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = ConversationDTO.class)))
    @GetMapping("/conversations/producteur/{producteurId}")
    public ResponseEntity<List<ConversationDTO>> getConversationsProducteur(
            @Parameter(description = "ID du producteur") @PathVariable Long producteurId) {
        List<Conversation> conversations = conversationService.getConversationsByProducteur(producteurId);
        List<ConversationDTO> conversationDTOs = conversations.stream()
                .map(conversationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conversationDTOs);
    }
    
    @Operation(
        summary = "Récupérer toutes les conversations d'un producteur basées sur les messages",
        description = "Récupère toutes les conversations d'un producteur en se basant sur les messages réels (envoyés et reçus)."
    )
    @ApiResponse(responseCode = "200", description = "Conversations récupérées avec succès")
    @GetMapping("/conversations/producteur/{producteurId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getConversationsProducteurFromMessages(
            @Parameter(description = "ID du producteur") @PathVariable Long producteurId) {
        List<Map<String, Object>> conversations = messageService.getAllConversationsByUserId(producteurId.intValue());
        return ResponseEntity.ok(conversations);
    }
    
    @Operation(
        summary = "Marquer un message comme lu",
        description = "Marque un message spécifique comme lu."
    )
    @ApiResponse(responseCode = "200", description = "Message marqué comme lu avec succès", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = MessageDTO.class)))
    @ApiResponse(responseCode = "404", description = "Message non trouvé")
    @PutMapping("/message/{messageId}/lu")
    public ResponseEntity<MessageDTO> marquerMessageCommeLu(
            @Parameter(description = "ID du message") @PathVariable Long messageId) {
        Message message = messageService.markAsRead(messageId.intValue());
        if (message != null) {
            return ResponseEntity.ok(messageMapper.toDTO(message));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}