package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Notification;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationChatService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private ConsommateurService consommateurService;
    
    @Autowired
    private ProducteurService producteurService;
    
    /**
     * Envoyer une notification lorsqu'un nouveau message est reçu
     */
    public void envoyerNotificationNouveauMessage(Long expediteurId, Long destinataireId, String contenuMessage) {
        // Créer une notification pour le destinataire
        Notification notification = new Notification();
        notification.setTitre("Nouveau message");
        notification.setMessage("Vous avez reçu un nouveau message: " + contenuMessage);
        notification.setTypeMessage(TypeMessage.INFO_SYSTEME);
        notification.setDateEnvoi(LocalDateTime.now());
        
        // Définir le destinataire de la notification
        if (destinataireId != null) {
            // Vérifier si le destinataire est un consommateur ou un producteur
            // Note: Cette logique devra être adaptée selon votre implémentation
        }
        
        notificationRepository.save(notification);
    }
    
    /**
     * Envoyer une notification à un consommateur
     */
    public void notifierConsommateur(Long consommateurId, String titre, String message) {
        Consommateur consommateur = consommateurService.getConsommateurById(consommateurId.intValue());
        if (consommateur != null) {
            Notification notification = new Notification();
            notification.setTitre(titre);
            notification.setMessage(message);
            notification.setTypeMessage(TypeMessage.INFO_SYSTEME);
            notification.setDateEnvoi(LocalDateTime.now());
            notification.setDestinataire(consommateur);
            
            notificationRepository.save(notification);
        }
    }
    
    /**
     * Envoyer une notification à un producteur
     */
    public void notifierProducteur(Long producteurId, String titre, String message) {
        Producteur producteur = producteurService.getProducteurById(producteurId.intValue());
        if (producteur != null) {
            Notification notification = new Notification();
            notification.setTitre(titre);
            notification.setMessage(message);
            notification.setTypeMessage(TypeMessage.INFO_SYSTEME);
            notification.setDateEnvoi(LocalDateTime.now());
            notification.setDestinataire(producteur);
            
            notificationRepository.save(notification);
        }
    }
}