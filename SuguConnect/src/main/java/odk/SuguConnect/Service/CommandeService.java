package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final ConsommateurRepository consommateurRepository;
    private final PaiementRepository paiementRepository;
    private final PanierRepository panierRepository;
    private final NotificationService notificationService;

    public CommandeService(CommandeRepository commandeRepository, ProduitRepository produitRepository, 
                          ConsommateurRepository consommateurRepository, PaiementRepository paiementRepository, 
                          PanierRepository panierRepository, NotificationService notificationService) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
        this.consommateurRepository = consommateurRepository;
        this.paiementRepository = paiementRepository;
        this.panierRepository = panierRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Commande passerCommande(int idConsommateur, ModePaiement modePaiement) {
        Consommateur consommateur = consommateurRepository.findById(idConsommateur)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));
        Panier panier = consommateur.getPanier();
        if (panier == null || panier.getPanierProduits().isEmpty()) {
            throw new EntityNotFoundException("Panier vide");
        }
        Commande commande = new Commande();
        commande.setConsommateur(consommateur);
        commande.setDateCommande(LocalDate.now());
        commande.setModePaiement(modePaiement);
        commande.setStatutCommande(StatutCommande.EN_ATTENTE);
        Double total = 0.0;
        for (PanierProduit panierProduit : panier.getPanierProduits()) {
            Produit produit = panierProduit.getProduit();
            if (produit.getStockDisponible() < produit.getQuantite()) {
                throw new IllegalArgumentException("Stock insuffisant");
            }
            CommandeProduit commandeProduit = new CommandeProduit();
            commandeProduit.setCommande(commande);
            commandeProduit.setProduit(produit);
            commandeProduit.setQuantite(panierProduit.getQuantite());
            commandeProduit.setPrixUnitaire(produit.getPrixUnitaire());
            commande.getCommandeProduits().add(commandeProduit);
            total += produit.getPrixUnitaire() * commandeProduit.getQuantite();
            produit.setStockDisponible(produit.getStockDisponible() - panierProduit.getQuantite());
            produitRepository.save(produit);
        }
        commandeRepository.save(commande);
        
        // Notifier le consommateur de la commande passée
        notificationService.notifierCommandePassee(idConsommateur, commande.getIdCommande(), total);
        
        // Notifier le producteur de la nouvelle commande
        Producteur producteur = commande.getCommandeProduits().get(0).getProduit().getProducteur();
        String messageProducteur = String.format("Nouvelle commande #%d reçue d'un montant de %.2f FCFA", 
                                                  commande.getIdCommande(), total);
        notificationService.creerNotification(producteur.getId(), 
                                            odk.SuguConnect.Enums.TypeMessage.COMMANDE_PASSEE, 
                                            messageProducteur, 
                                            "/commandes/" + commande.getIdCommande());
        
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMethodePaiement(modePaiement);
        paiement.setMontant(total);
        paiement.setStatutPaiement(StatutPaiement.INITIE);
        paiement.setDatePaiement(LocalDate.now());
        paiementRepository.save(paiement);
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        panierRepository.save(panier);
        return commande;
    }
    public List<Commande> voirCommandesParConsommateur(int idConsommateur){
        Consommateur consommateur = consommateurRepository.findById(idConsommateur)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));

        List<Commande> commandes = commandeRepository.findByConsommateur(consommateur);

        if (commandes.isEmpty()) {
            throw new EntityNotFoundException("Aucune commande trouvée pour ce consommateur");
        }
        return commandes;
    }
    public Commande voirCommandeParId(int commandeId) {
        return commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
    }
    public List<Commande> voirToutesLesCommandes() {
        return commandeRepository.findAll();
    }
    
    /**
     * Changer le statut d'une commande (par producteur ou admin)
     */
    @Transactional
    public Commande changerStatutCommande(int commandeId, int producteurId, StatutCommande nouveauStatut, String motifRejet) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
        
        // Vérifier que le producteur est bien le propriétaire des produits de la commande
        Producteur producteurCommande = commande.getCommandeProduits().get(0).getProduit().getProducteur();
        if (producteurCommande.getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cette commande");
        }
        
        StatutCommande ancienStatut = commande.getStatutCommande();
        commande.setStatutCommande(nouveauStatut);
        
        if (nouveauStatut == StatutCommande.REFUSEE) {
            commande.setMotifRejet(motifRejet);
            // Notifier le consommateur du refus
            notificationService.notifierCommandeRefusee(
                commande.getConsommateur().getId(), 
                commandeId, 
                motifRejet
            );
        } else if (nouveauStatut == StatutCommande.VALIDEE) {
            // Notifier le consommateur de la validation
            notificationService.notifierCommandeValidee(
                commande.getConsommateur().getId(), 
                commandeId
            );
        } else if (nouveauStatut == StatutCommande.EN_LIVRAISON) {
            // Notifier le consommateur que la commande est en livraison
            notificationService.notifierCommandeEnLivraison(
                commande.getConsommateur().getId(), 
                commandeId
            );
        } else if (nouveauStatut == StatutCommande.LIVREE) {
            // Notifier le consommateur de la livraison
            notificationService.notifierCommandeLivree(
                commande.getConsommateur().getId(), 
                commandeId
            );
        }
        
        return commandeRepository.save(commande);
    }
    
    /**
     * Valider la réception de la commande par le consommateur
     */
    @Transactional
    public Commande validerReceptionCommande(int commandeId, int consommateurId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
        
        // Vérifier que le consommateur est bien le propriétaire de la commande
        if (commande.getConsommateur().getId() != consommateurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à valider cette commande");
        }
        
        // Vérifier que la commande est livrée
        if (commande.getStatutCommande() != StatutCommande.LIVREE) {
            throw new IllegalStateException("Vous ne pouvez valider que les commandes livrées");
        }
        
        // Vérifier qu'elle n'a pas déjà été validée
        if (commande.isReceptionValidee()) {
            throw new IllegalStateException("Cette commande a déjà été validée");
        }
        
        commande.setReceptionValidee(true);
        commande.setDateReceptionValidee(LocalDate.now());
        
        // Notifier le producteur que le client a validé la réception
        Producteur producteur = commande.getCommandeProduits().get(0).getProduit().getProducteur();
        notificationService.notifierRevenuProducteur(
            producteur.getId(), 
            commandeId, 
            commande.getMontantTotal()
        );
        
        return commandeRepository.save(commande);
    }
}
