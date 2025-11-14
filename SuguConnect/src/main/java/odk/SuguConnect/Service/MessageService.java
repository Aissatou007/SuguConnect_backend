package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Entity.Conversation;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Repository.MessageRepository;
import odk.SuguConnect.Mapper.MessageMapper;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Service.NotificationChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ConsommateurRepository consommateurRepository;
    
    @Autowired
    private ProducteurRepository producteurRepository;
    
    @Autowired
    private NotificationChatService notificationChatService;
    
    public Message creerMessage(Long conversationId, Long expediteurId, Long destinataireId, 
                               String contenu, TypeMessage typeMessage, String cheminFichier) {
        // Vérifier si la conversation existe, sinon la créer
        Conversation conversation = conversationService.getConversationById(conversationId)
                .orElseGet(() -> conversationService.creerConversation(
                    expediteurId, destinataireId, null));
        
        // Créer le message
        Message message = new Message();
        message.setContenu(contenu);
        message.setTypeMessage(typeMessage);
        message.setCheminFichier(cheminFichier);
        message.setConversation(conversation);
        
        // Définir l'expéditeur (consommateur)
        Optional<Consommateur> expediteur = consommateurRepository.findById(expediteurId.intValue());
        if (expediteur.isPresent()) {
            message.setExpediteur(expediteur.get());
        }
        
        // Définir le destinataire (producteur)
        Optional<Producteur> destinataire = producteurRepository.findById(destinataireId.intValue());
        if (destinataire.isPresent()) {
            message.setDestinataire(destinataire.get());
        }
        
        Message savedMessage = messageRepository.save(message);
        
        // Envoyer une notification au destinataire
        if (savedMessage != null) {
            notificationChatService.envoyerNotificationNouveauMessage(expediteurId, destinataireId, contenu);
        }
        
        return savedMessage;
    }
    
    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderByDateEnvoiAsc(conversationId);
    }
    
    public List<Message> getMessagesBetweenUsers(Long expediteurId, Long destinataireId) {
        return messageRepository.findByExpediteurIdAndDestinataireIdOrderByDateEnvoiAsc(
                expediteurId, destinataireId);
    }
    
    public Message marquerCommeLu(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setLu(true);
            return messageRepository.save(message);
        }
        return null;
    }
}