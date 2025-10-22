package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Notification;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Interface.Utilisateur;
import odk.SuguConnect.Repository.NotificationRepository;
import odk.SuguConnect.Repository.UtilisateurRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Créer une notification pour un utilisateur
     */
    @Transactional
    public Notification creerNotification(int destinataireId, TypeMessage typeMessage, 
                                         String message, String action, int dureeVieHeures) {
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Notification notification = new Notification();
        notification.setTitre(typeMessage.getTitre());
        notification.setMessage(message != null ? message : typeMessage.getDescription());
        notification.setTypeMessage(typeMessage);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setDateExpiration(LocalDateTime.now().plusHours(dureeVieHeures));
        notification.setAction(action);
        notification.setDestinataire(destinataire);
        notification.setLu(false);

        return notificationRepository.save(notification);
    }

    /**
     * Créer une notification avec durée de vie par défaut (7 jours)
     */
    @Transactional
    public Notification creerNotification(int destinataireId, TypeMessage typeMessage, String message, String action) {
        return creerNotification(destinataireId, typeMessage, message, action, 168); // 7 jours = 168 heures
    }

    /**
     * Créer une notification simple
     */
    @Transactional
    public Notification creerNotification(int destinataireId, TypeMessage typeMessage, String message) {
        return creerNotification(destinataireId, typeMessage, message, null, 168);
    }

    /**
     * Notification de commande passée
     */
    @Transactional
    public void notifierCommandePassee(int consommateurId, int commandeId, double montant) {
        String message = String.format("Votre commande #%d d'un montant de %.2f FCFA a été passée avec succès.", 
                commandeId, montant);
        String action = "/commandes/" + commandeId;
        creerNotification(consommateurId, TypeMessage.COMMANDE_PASSEE, message, action, 720); // 30 jours
    }

    /**
     * Notification de commande validée
     */
    @Transactional
    public void notifierCommandeValidee(int consommateurId, int commandeId) {
        String message = String.format("Votre commande #%d a été validée et sera bientôt livrée.", commandeId);
        String action = "/commandes/" + commandeId;
        creerNotification(consommateurId, TypeMessage.COMMANDE_VALIDEE, message, action, 720);
    }

    /**
     * Notification de commande refusée
     */
    @Transactional
    public void notifierCommandeRefusee(int consommateurId, int commandeId, String raison) {
        String message = String.format("Votre commande #%d a été refusée. Raison : %s", commandeId, raison);
        String action = "/commandes/" + commandeId;
        creerNotification(consommateurId, TypeMessage.COMMANDE_REFUSEE, message, action, 720);
    }

    /**
     * Notification de commande en livraison
     */
    @Transactional
    public void notifierCommandeEnLivraison(int consommateurId, int commandeId) {
        String message = String.format("Votre commande #%d est en cours de livraison.", commandeId);
        String action = "/commandes/" + commandeId;
        creerNotification(consommateurId, TypeMessage.COMMANDE_EN_LIVRAISON, message, action, 168);
    }

    /**
     * Notification de commande livrée
     */
    @Transactional
    public void notifierCommandeLivree(int consommateurId, int commandeId) {
        String message = String.format("Votre commande #%d a été livrée. Merci de confirmer la réception.", commandeId);
        String action = "/commandes/" + commandeId + "/confirmer";
        creerNotification(consommateurId, TypeMessage.COMMANDE_LIVREE, message, action, 168);
    }

    /**
     * Notification de paiement reçu
     */
    @Transactional
    public void notifierPaiementRecu(int consommateurId, int paiementId, double montant) {
        String message = String.format("Votre paiement de %.2f FCFA a été reçu avec succès.", montant);
        String action = "/paiements/" + paiementId;
        creerNotification(consommateurId, TypeMessage.PAIEMENT_RECU, message, action, 720);
    }

    /**
     * Notification de remboursement effectué
     */
    @Transactional
    public void notifierRemboursementEffectue(int consommateurId, double montant) {
        String message = String.format("Un remboursement de %.2f FCFA a été effectué sur votre compte.", montant);
        creerNotification(consommateurId, TypeMessage.REMBOURSEMENT_EFFECTUE, message, null, 720);
    }

    /**
     * Notification de revenu producteur
     */
    @Transactional
    public void notifierRevenuProducteur(int producteurId, int commandeId, double montant) {
        String message = String.format("Félicitations ! Vous avez reçu %.2f FCFA pour la commande #%d validée par le client.", 
                montant, commandeId);
        String action = "/revenus/" + commandeId;
        creerNotification(producteurId, TypeMessage.REVENU_PRODUCTEUR, message, action, 720);
    }

    /**
     * Notification de compte validé
     */
    @Transactional
    public void notifierCompteValide(int producteurId) {
        String message = "Félicitations ! Votre compte producteur a été validé. Vous pouvez maintenant ajouter vos produits.";
        String action = "/produits/ajouter";
        creerNotification(producteurId, TypeMessage.COMPTE_VALIDE, message, action, 168);
    }

    /**
     * Notification de compte refusé
     */
    @Transactional
    public void notifierCompteRefuse(int producteurId, String raison) {
        String message = String.format("Votre demande de compte producteur a été refusée. Raison : %s", raison);
        creerNotification(producteurId, TypeMessage.COMPTE_REFUSE, message, null, 720);
    }

    /**
     * Notification de stock faible
     */
    @Transactional
    public void notifierStockFaible(int producteurId, int produitId, String nomProduit, int stockRestant) {
        String message = String.format("Attention ! Le stock de votre produit '%s' est faible (%d unités restantes).", 
                nomProduit, stockRestant);
        String action = "/produits/" + produitId + "/reapprovisionner";
        creerNotification(producteurId, TypeMessage.STOCK_FAIBLE, message, action, 72); // 3 jours
    }

    /**
     * Notification de produit épuisé
     */
    @Transactional
    public void notifierProduitEpuise(int producteurId, int produitId, String nomProduit) {
        String message = String.format("Votre produit '%s' est épuisé. Veuillez réapprovisionner le stock.", nomProduit);
        String action = "/produits/" + produitId + "/reapprovisionner";
        creerNotification(producteurId, TypeMessage.PRODUIT_EPUISE, message, action, 72);
    }

    /**
     * Notification admin - Nouvelle inscription producteur
     */
    @Transactional
    public void notifierAdminNouvelleInscription(List<Integer> adminIds, int producteurId, String nomProducteur) {
        String message = String.format("Nouveau producteur inscrit : %s. Validation requise.", nomProducteur);
        String action = "/admin/producteurs/" + producteurId + "/valider";
        
        for (Integer adminId : adminIds) {
            creerNotification(adminId, TypeMessage.NOUVELLE_INSCRIPTION_PRODUCTEUR, message, action, 168);
        }
    }

    /**
     * Notification admin - Nouvelle commande
     */
    @Transactional
    public void notifierAdminNouvelleCommande(List<Integer> adminIds, int commandeId, double montant) {
        String message = String.format("Nouvelle commande #%d d'un montant de %.2f FCFA.", commandeId, montant);
        String action = "/admin/commandes/" + commandeId;
        
        for (Integer adminId : adminIds) {
            creerNotification(adminId, TypeMessage.NOUVELLE_COMMANDE, message, action, 168);
        }
    }

    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    public List<Notification> getNotifications(int utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        return notificationRepository.findNotificationsNonExpirees(utilisateur, LocalDateTime.now());
    }

    /**
     * Récupérer les notifications non lues
     */
    public List<Notification> getNotificationsNonLues(int utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        return notificationRepository.findByDestinataireAndLuFalse(utilisateur);
    }

    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public Notification marquerCommeLue(int notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée"));
        notification.setLu(true);
        return notificationRepository.save(notification);
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    @Transactional
    public void marquerToutesCommeLues(int utilisateurId) {
        List<Notification> notifications = getNotificationsNonLues(utilisateurId);
        notifications.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(notifications);
    }

    /**
     * Compter les notifications non lues
     */
    public long compterNotificationsNonLues(int utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        return notificationRepository.countByDestinataireAndLuFalse(utilisateur);
    }

    /**
     * Supprimer une notification
     */
    @Transactional
    public void supprimerNotification(int notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new EntityNotFoundException("Notification non trouvée");
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Nettoyage automatique des notifications expirées (tous les jours à 2h du matin)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void nettoyerNotificationsExpirees() {
        List<Notification> notificationsExpirees = notificationRepository.findNotificationsExpirees(LocalDateTime.now());
        notificationRepository.deleteAll(notificationsExpirees);
        System.out.println("Nettoyage automatique : " + notificationsExpirees.size() + " notifications expirées supprimées.");
    }

    /**
     * Récupérer les notifications récentes (7 derniers jours)
     */
    public List<Notification> getNotificationsRecentes(int utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(7);
        return notificationRepository.findNotificationsRecentes(utilisateur, dateDebut);
    }
}
