package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final ConsommateurRepository consommateurRepository;
    private final PaiementRepository paiementRepository;
    private final PanierRepository panierRepository;
    private final NotificationService notificationService;

    @Transactional
    public Commande passerCommande(int idConsommateur, ModePaiement modePaiement) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        Panier panier = validerPanier(consommateur);
        
        // Créer la commande
        Commande commande = creerCommande(consommateur, modePaiement);
        
        // Traiter les produits du panier
        double montantTotal = traiterProduitsPanier(panier, commande);
        commande.setMontantTotal(montantTotal);
        commandeRepository.save(commande);
        
        // Créer le paiement
        Paiement paiement = creerPaiement(commande, modePaiement, montantTotal);
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        
        // Envoyer les notifications
        envoyerNotificationsCommande(consommateur.getId(), commande, montantTotal);
        
        // Vider le panier
        viderPanier(panier);
        
        return commande;
    }
    public List<Commande> voirCommandesParConsommateur(int idConsommateur) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        List<Commande> commandes = commandeRepository.findByConsommateur(consommateur);
        
        if (commandes.isEmpty()) {
            throw new EntityNotFoundException("Aucune commande trouvée pour ce consommateur");
        }
        return commandes;
    }
    
    public Commande voirCommandeParId(int commandeId) {
        return findCommandeById(commandeId);
    }
    
    public List<Commande> voirToutesLesCommandes() {
        return commandeRepository.findAll();
    }
    
    @Transactional
    public Commande changerStatutCommande(int commandeId, int producteurId, StatutCommande nouveauStatut, String motifRejet) {
        Commande commande = findCommandeById(commandeId);
        verifierProprietaireCommande(commande, producteurId);
        
        // Mettre à jour le statut
        commande.setStatutCommande(nouveauStatut);
        if (nouveauStatut == StatutCommande.DECLINEE) {
            commande.setMotifRejet(motifRejet);
        }
        
        // Notifier selon le statut
        notifierChangementStatut(commande, nouveauStatut, motifRejet);
        
        return commandeRepository.save(commande);
    }
    
    @Transactional
    public Commande validerReceptionCommande(int commandeId, int consommateurId) {
        Commande commande = findCommandeById(commandeId);
        verifierProprietaireReception(commande, consommateurId);
        verifierStatutLivraison(commande);
        verifierNonValidee(commande);
        
        // Valider la réception
        commande.setReceptionValidee(true);
        commande.setDateReceptionValidee(LocalDate.now());
        
        // Notifier le producteur
        Producteur producteur = getProducteurCommande(commande);
        notificationService.notifierRevenuProducteur(
            producteur.getId(), 
            commandeId, 
            commande.getMontantTotal()
        );
        
        return commandeRepository.save(commande);
    }
    
    // ========== Méthodes privées utilitaires ==========
    
    private Consommateur findConsommateurById(int id) {
        return consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur introuvable"));
    }
    
    private Commande findCommandeById(int id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable"));
    }
    
    private Panier validerPanier(Consommateur consommateur) {
        Panier panier = consommateur.getPanier();
        if (panier == null || panier.getPanierProduits().isEmpty()) {
            throw new EntityNotFoundException("Panier vide. Veuillez d'abord ajouter des produits à votre panier.");
        }
        return panier;
    }
    
    private Commande creerCommande(Consommateur consommateur, ModePaiement modePaiement) {
        Commande commande = new Commande();
        commande.setConsommateur(consommateur);
        commande.setDateCommande(LocalDate.now());
        commande.setModePaiement(modePaiement);
        commande.setStatutCommande(StatutCommande.EN_ATTENTE);
        return commande;
    }
    
    private double traiterProduitsPanier(Panier panier, Commande commande) {
        double total = 0.0;
        
        for (PanierProduit panierProduit : panier.getPanierProduits()) {
            Produit produit = panierProduit.getProduit();
            int quantite = panierProduit.getQuantite();
            
            // Vérifier et réduire le stock
            verifierEtReduireStock(produit, quantite);
            
            // Créer l'article de commande
            CommandeProduit commandeProduit = creerCommandeProduit(commande, panierProduit);
            commande.getCommandeProduits().add(commandeProduit);
            
            total += panierProduit.getPrixUnitaire() * quantite;
        }
        
        return total;
    }
    
    private void verifierEtReduireStock(Produit produit, int quantite) {
        if (produit.getStockDisponible() < quantite) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant pour %s. Disponible: %d, Demandé: %d",
                    produit.getNom(), produit.getStockDisponible(), quantite)
            );
        }
        produit.setStockDisponible(produit.getStockDisponible() - quantite);
        produitRepository.save(produit);
    }
    
    private CommandeProduit creerCommandeProduit(Commande commande, PanierProduit panierProduit) {
        CommandeProduit commandeProduit = new CommandeProduit();
        commandeProduit.setCommande(commande);
        commandeProduit.setProduit(panierProduit.getProduit());
        commandeProduit.setQuantite(panierProduit.getQuantite());
        commandeProduit.setPrixUnitaire((float) panierProduit.getPrixUnitaire());
        return commandeProduit;
    }
    
    private Paiement creerPaiement(Commande commande, ModePaiement modePaiement, double montant) {
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMethodePaiement(modePaiement);
        paiement.setMontant(montant);
        paiement.setStatutPaiement(StatutPaiement.INITIE);
        paiement.setDatePaiement(LocalDate.now());
        return paiementRepository.save(paiement);
    }
    
    private void envoyerNotificationsCommande(int consommateurId, Commande commande, double montant) {
        // Notifier le consommateur
        notificationService.notifierCommandePassee(consommateurId, commande.getIdCommande(), montant);
        
        // Notifier le producteur
        Producteur producteur = getProducteurCommande(commande);
        String message = String.format("Nouvelle commande #%d reçue d'un montant de %.2f FCFA",
            commande.getIdCommande(), montant);
        notificationService.creerNotification(
            producteur.getId(),
            TypeMessage.COMMANDE_PASSEE,
            message,
            "/commandes/" + commande.getIdCommande()
        );
    }
    
    private void viderPanier(Panier panier) {
        panier.getPanierProduits().clear();
        panier.getProduits().clear();
        panierRepository.save(panier);
    }
    
    private Producteur getProducteurCommande(Commande commande) {
        return commande.getCommandeProduits().get(0).getProduit().getProducteur();
    }
    
    private void verifierProprietaireCommande(Commande commande, int producteurId) {
        Producteur producteur = getProducteurCommande(commande);
        if (producteur.getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cette commande");
        }
    }
    
    private void notifierChangementStatut(Commande commande, StatutCommande nouveauStatut, String motifRejet) {
        int consommateurId = commande.getConsommateur().getId();
        int commandeId = commande.getIdCommande();
        
        switch (nouveauStatut) {
            case DECLINEE -> notificationService.notifierCommandeRefusee(consommateurId, commandeId, motifRejet);
            case VALIDEE -> notificationService.notifierCommandeValidee(consommateurId, commandeId);
            case EN_LIVRAISON -> notificationService.notifierCommandeEnLivraison(consommateurId, commandeId);
            case LIVREE -> notificationService.notifierCommandeLivree(consommateurId, commandeId);
        }
    }
    
    private void verifierProprietaireReception(Commande commande, int consommateurId) {
        if (commande.getConsommateur().getId() != consommateurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à valider cette commande");
        }
    }
    
    private void verifierStatutLivraison(Commande commande) {
        if (commande.getStatutCommande() != StatutCommande.LIVREE) {
            throw new IllegalStateException("Vous ne pouvez valider que les commandes livrées");
        }
    }
    
    private void verifierNonValidee(Commande commande) {
        if (commande.isReceptionValidee()) {
            throw new IllegalStateException("Cette commande a déjà été validée");
        }
    }
}
