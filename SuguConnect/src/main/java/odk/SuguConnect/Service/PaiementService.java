package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Repository.CommandeRepository;
import odk.SuguConnect.Repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsable UNIQUEMENT de la gestion des paiements
 * Respecte le principe SRP - Single Responsibility Principle
 */
@Service
@RequiredArgsConstructor
public class PaiementService {
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final NotificationService notificationService;
    
    /**
     * Récupérer tous les paiements
     * Responsabilité: Lecture de tous les paiements
     */
    public List<Paiement> recupererTousLesPaiements() {
        return paiementRepository.findAll();
    }
    
    /**
     * Récupérer un paiement par ID
     * Responsabilité: Lecture d'un paiement spécifique
     */
    public Paiement recupererPaiementParId(int id) {
        return findPaiementById(id);
    }
    
    /**
     * Récupérer les paiements d'une commande
     * Responsabilité: Lecture des paiements d'une commande
     */
    public Paiement recupererPaiementParCommande(int commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
        
        if (commande.getPaiement() == null) {
            throw new EntityNotFoundException("Aucun paiement trouvé pour cette commande");
        }
        
        return commande.getPaiement();
    }
    
    /**
     * Valider un paiement
     * Responsabilité: Marquer un paiement comme validé
     */
    @Transactional
    public Paiement validerPaiement(int paiementId, String referenceTransaction) {
        Paiement paiement = findPaiementById(paiementId);
        
        // Vérifier que le paiement n'est pas déjà validé
        if (paiement.getStatutPaiement() == StatutPaiement.VALIDE) {
            throw new IllegalStateException("Ce paiement a déjà été validé");
        }
        
        // Mettre à jour le statut
        paiement.setStatutPaiement(StatutPaiement.VALIDE);
        paiement.setDatePaiement(LocalDate.now());
        paiementRepository.save(paiement);
        
        // Notifier le consommateur
        Commande commande = paiement.getCommande();
        if (commande != null && commande.getConsommateur() != null) {
            notificationService.notifierPaiementRecu(
                commande.getConsommateur().getId(),
                commande.getIdCommande(),
                paiement.getMontant()
            );
        }
        
        return paiement;
    }
    
    /**
     * Marquer un paiement comme échoué
     * Responsabilité: Marquer un paiement comme échoué
     */
    @Transactional
    public Paiement marquerPaiementEchoue(int paiementId, String motifEchec) {
        Paiement paiement = findPaiementById(paiementId);
        
        paiement.setStatutPaiement(StatutPaiement.ECHOUE);
        paiementRepository.save(paiement);
        
        // Annuler la commande associée
        Commande commande = paiement.getCommande();
        if (commande != null) {
            commande.setStatutCommande(StatutCommande.DECLINEE);
            commande.setMotifRejet("Paiement échoué: " + motifEchec);
            commandeRepository.save(commande);
            
            // Notifier le consommateur
            if (commande.getConsommateur() != null) {
                String message = String.format(
                    "Le paiement de votre commande #%d a échoué. Raison: %s",
                    commande.getIdCommande(),
                    motifEchec
                );
                notificationService.creerNotification(
                    commande.getConsommateur().getId(),
                    odk.SuguConnect.Enums.TypeMessage.PAIEMENT_RECU,
                    message,
                    "/commandes/" + commande.getIdCommande()
                );
            }
        }
        
        return paiement;
    }
    
    /**
     * Rembourser un paiement
     * Responsabilité: Gérer le remboursement d'un paiement
     */
    @Transactional
    public Paiement rembourserPaiement(int paiementId, String motifRemboursement) {
        Paiement paiement = findPaiementById(paiementId);
        
        // Vérifier que le paiement est validé
        if (paiement.getStatutPaiement() != StatutPaiement.VALIDE) {
            throw new IllegalStateException("Seul un paiement validé peut être remboursé");
        }
        
        paiement.setStatutPaiement(StatutPaiement.REMBOURSE);
        paiementRepository.save(paiement);
        
        // Notifier le consommateur du remboursement
        Commande commande = paiement.getCommande();
        if (commande != null && commande.getConsommateur() != null) {
            notificationService.notifierRemboursementEffectue(
                commande.getConsommateur().getId(),
                commande.getIdCommande(),
                paiement.getMontant()
            );
        }
        
        return paiement;
    }
    
    /**
     * Initier un paiement mobile (Orange Money / Wave)
     * Responsabilité: Simuler l'initiation d'un paiement mobile
     * Note: Dans une vraie implémentation, ceci appellerait l'API du fournisseur
     */
    @Transactional
    public String initierPaiementMobile(int paiementId, String numeroTelephone) {
        Paiement paiement = findPaiementById(paiementId);
        
        // Vérifier que le paiement est en attente
        if (paiement.getStatutPaiement() != StatutPaiement.INITIE && 
            paiement.getStatutPaiement() != StatutPaiement.EN_ATTENTE) {
            throw new IllegalStateException("Ce paiement ne peut plus être initié");
        }
        
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
        paiementRepository.save(paiement);
        
        // TODO: Intégration avec Orange Money / Wave API
        // Pour l'instant, retourner un message de simulation
        String provider = paiement.getMethodePaiement().toString();
        String message = String.format(
            "Paiement de %.2f FCFA initié via %s au numéro %s. " +
            "Veuillez composer *144# (Orange Money) ou ouvrir Wave pour confirmer.",
            paiement.getMontant(),
            provider,
            numeroTelephone
        );
        
        return message;
    }
    
    /**
     * Webhook pour Orange Money / Wave
     * Responsabilité: Recevoir les notifications de paiement des fournisseurs
     * Note: À implémenter lors de l'intégration réelle avec les APIs
     */
    @Transactional
    public void traiterWebhookPaiement(String referenceTransaction, String statut, int paiementId) {
        Paiement paiement = findPaiementById(paiementId);
        
        switch (statut.toUpperCase()) {
            case "SUCCESS", "COMPLETED" -> validerPaiement(paiementId, referenceTransaction);
            case "FAILED", "CANCELLED" -> marquerPaiementEchoue(paiementId, "Transaction annulée par l'utilisateur");
            default -> paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
        }
        
        paiementRepository.save(paiement);
    }
    
    // ========== Méthodes privées utilitaires ==========
    
    private Paiement findPaiementById(int id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));
    }
}
