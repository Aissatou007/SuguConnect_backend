package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Repository.CommandeRepository;
import odk.SuguConnect.Repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementService {
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final NotificationService notificationService;

    public Paiement creerPaiement(int commandeId, ModePaiement modePaiement, double montant, String numeroTelephone, int consommateurId) {
        System.out.println("=== Création de paiement ===");
        System.out.println("Commande ID: " + commandeId);
        System.out.println("Consommateur ID: " + consommateurId);
        System.out.println("Montant: " + montant);
        System.out.println("Mode de paiement: " + modePaiement);
        System.out.println("Numéro de téléphone: " + numeroTelephone);
        
        try {
            // Vérifier que la commande existe
            Commande commande = commandeRepository.findById(commandeId)
                    .orElseThrow(() -> new EntityNotFoundException("Commande introuvable avec ID: " + commandeId));
            
            System.out.println("Commande trouvée: " + commande.getIdCommande());
            System.out.println("Propriétaire de la commande: " + commande.getConsommateur().getId());
            
            // Vérifier que le consommateur est bien le propriétaire de la commande
            if (commande.getConsommateur().getId() != consommateurId) {
                System.out.println("ERREUR: Le consommateur " + consommateurId + " n'est pas le propriétaire de la commande " + commandeId);
                throw new SecurityException("Vous n'êtes pas autorisé à créer un paiement pour cette commande");
            }
            
            System.out.println("Autorisation vérifiée avec succès");
            
            // Créer le paiement
            Paiement paiement = new Paiement();
            paiement.setCommande(commande);
            paiement.setMethodePaiement(modePaiement);
            paiement.setMontant(montant);
            paiement.setStatutPaiement(StatutPaiement.INITIE);
            paiement.setDatePaiement(LocalDate.now());
            paiement.setNumeroTelephone(numeroTelephone);
            
            // Sauvegarder le paiement
            Paiement savedPaiement = paiementRepository.save(paiement);
            
            System.out.println("Paiement créé avec ID: " + savedPaiement.getIdPaiement());
            
            // Associer le paiement à la commande
            commande.setPaiement(savedPaiement);
            commandeRepository.save(commande);
            
            System.out.println("Paiement associé à la commande");
            
            return savedPaiement;
        } catch (Exception e) {
            System.out.println("ERREUR dans creerPaiement: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<Paiement> recupererTousLesPaiements() {
        return paiementRepository.findAll();
    }
    
    public Paiement recupererPaiementParId(int id) {
        return findPaiementById(id);
    }
    
    public Paiement recupererPaiementParCommande(int commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
        
        if (commande.getPaiement() == null) {
            throw new EntityNotFoundException("Aucun paiement trouvé pour cette commande");
        }
        
        return commande.getPaiement();
    }
    
    // Valider un paiement
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
        if (referenceTransaction != null) {
            paiement.setReferenceTransaction(referenceTransaction);
        }
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
    
    //Marquer un paiement comme échoué

    @Transactional
    public Paiement marquerPaiementEchoue(int paiementId, String motifEchec) {
        Paiement paiement = findPaiementById(paiementId);
        
        paiement.setStatutPaiement(StatutPaiement.ECHOUE);
        paiementRepository.save(paiement);
        
        // Annuler la commande associée
        Commande commande = paiement.getCommande();
        if (commande != null) {
            commande.setStatutCommande(odk.SuguConnect.Enums.StatutCommande.DECLINEE);
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
    
    // Rembourser un paiement
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
                paiement.getMontant()
            );
        }
        
        return paiement;
    }

    @Transactional
    public String initierPaiementMobile(int paiementId, String numeroTelephone) {
        Paiement paiement = findPaiementById(paiementId);
        
        // Vérifier que le paiement est en attente
        if (paiement.getStatutPaiement() != StatutPaiement.INITIE && 
            paiement.getStatutPaiement() != StatutPaiement.EN_ATTENTE) {
            throw new IllegalStateException("Ce paiement ne peut plus être initié");
        }
        
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
        if (numeroTelephone != null) {
            paiement.setNumeroTelephone(numeroTelephone);
        }
        paiementRepository.save(paiement);
        
        // TODO: Intégration avec Orange Money / Wave API
        String provider = paiement.getMethodePaiement().toString();
        String message = String.format(
            "Paiement de %.2f FCFA initié via %s au numéro %s. " +
            "Veuillez composer *144# (Orange Money)  pour confirmer.",
            paiement.getMontant(),
            provider,
            numeroTelephone
        );
        
        return message;
    }
    
    /**
     * Webhook pour Orange Money
     * Note: À implémenter lors de l'intégration réelle avec les APIs
     */
    @Transactional
    public void traiterWebhookPaiement(String referenceTransaction, String statut, int paiementId) {
        Paiement paiement = findPaiementById(paiementId);
        
        switch (statut.toUpperCase()) {
            case "SUCCESS":
            case "COMPLETED":
                validerPaiement(paiementId, referenceTransaction);
                break;
            case "FAILED":
            case "CANCELLED":
                marquerPaiementEchoue(paiementId, "Transaction annulée par l'utilisateur");
                break;
            default:
                paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
                break;
        }
        
        paiementRepository.save(paiement);
    }
    
    // ========== Méthodes privées utilitaires ==========
    
    private Paiement findPaiementById(int id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable"));
    }
}