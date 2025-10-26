package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.PasserCommandePanierRequestDTO;
import odk.SuguConnect.DTO.Request.PasserCommandeRequestDTO;
import odk.SuguConnect.DTO.Request.ProduitCommandeDTO;
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
    private final PanierProduitRepository panierProduitRepository;
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
    
    /**
     * Passer une commande avec des produits spécifiques (sans utiliser le panier)
     * Permet au consommateur de choisir directement les produits à commander
     */
    @Transactional
    public Commande passerCommandeAvecProduits(int idConsommateur, PasserCommandeRequestDTO request) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        
        // Créer la commande
        Commande commande = creerCommande(consommateur, request.modePaiement());
        
        // Traiter les produits spécifiés
        double montantTotal = traiterProduitsSpecifiques(request.produits(), commande);
        commande.setMontantTotal(montantTotal);
        commandeRepository.save(commande);
        
        // Créer le paiement
        Paiement paiement = creerPaiement(commande, request.modePaiement(), montantTotal);
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        
        // Envoyer les notifications
        envoyerNotificationsCommande(consommateur.getId(), commande, montantTotal);
        
        return commande;
    }
    
    @Transactional
    public Commande passerCommandeAvecProduitsDuPanier(int idConsommateur, PasserCommandePanierRequestDTO request) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        Panier panier = validerPanier(consommateur);
        
        // Créer la commande
        Commande commande = creerCommande(consommateur, request.modePaiement());
        
        // Traiter les produits spécifiés du panier
        double montantTotal = traiterProduitsSpecifiquesDuPanier(request.produits(), commande, panier);
        commande.setMontantTotal(montantTotal);
        commandeRepository.save(commande);
        
        // Créer le paiement
        Paiement paiement = creerPaiement(commande, request.modePaiement(), montantTotal);
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        
        // Envoyer les notifications
        envoyerNotificationsCommande(consommateur.getId(), commande, montantTotal);
        
        // Retirer les produits commandés du panier
        retirerProduitsDuPanier(panier, request.produits());
        
        return commande;
    }
    
    /**
     * Traiter les produits spécifiés dans la requête de commande
     */
    private double traiterProduitsSpecifiques(List<ProduitCommandeDTO> produits, Commande commande) {
        double total = 0.0;
        
        for (ProduitCommandeDTO produitDTO : produits) {
            Produit produit = findProduitById(produitDTO.produitId());
            int quantite = produitDTO.quantite();
            
            // Vérifier et réduire le stock
            verifierEtReduireStock(produit, quantite);
            
            // Créer l'article de commande
            CommandeProduit commandeProduit = creerCommandeProduitDirect(commande, produit, quantite);
            commande.getCommandeProduits().add(commandeProduit);
            
            total += produit.getPrixUnitaire() * quantite;
        }
        
        return total;
    }
    
    /**
     * Traiter les produits spécifiés dans la requête de commande à partir du panier
     */
    private double traiterProduitsSpecifiquesDuPanier(List<ProduitCommandeDTO> produits, Commande commande, Panier panier) {
        double total = 0.0;
        
        for (ProduitCommandeDTO produitDTO : produits) {
            // Trouver le produit dans le panier
            PanierProduit panierProduit = trouverProduitDansPanier(panier, produitDTO.produitId());
            int quantite = produitDTO.quantite();
            
            // Vérifier que la quantité demandée est disponible dans le panier
            if (quantite > panierProduit.getQuantite()) {
                throw new IllegalArgumentException(
                    String.format("Quantité demandée (%d) supérieure à la quantité dans le panier (%d) pour le produit %s",
                        quantite, panierProduit.getQuantite(), panierProduit.getProduit().getNom())
                );
            }
            
            // Vérifier et réduire le stock
            Produit produit = panierProduit.getProduit();
            verifierEtReduireStock(produit, quantite);
            
            // Créer l'article de commande
            CommandeProduit commandeProduit = creerCommandeProduitDirect(commande, produit, quantite);
            commande.getCommandeProduits().add(commandeProduit);
            
            total += produit.getPrixUnitaire() * quantite;
        }
        
        return total;
    }
    
    private PanierProduit trouverProduitDansPanier(Panier panier, int produitId) {
        return panier.getPanierProduits().stream()
                .filter(pp -> pp.getProduit().getId() == produitId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Le produit avec ID %d n'est pas dans le panier", produitId)));
    }
    
    private void retirerProduitsDuPanier(Panier panier, List<ProduitCommandeDTO> produits) {
        for (ProduitCommandeDTO produitDTO : produits) {
            PanierProduit panierProduit = trouverProduitDansPanier(panier, produitDTO.produitId());
            int quantiteRestante = panierProduit.getQuantite() - produitDTO.quantite();
            
            if (quantiteRestante <= 0) {
                // Retirer complètement le produit du panier
                panier.getPanierProduits().remove(panierProduit);
                panierProduitRepository.delete(panierProduit);
                panier.getProduits().removeIf(p -> p.getId() == produitDTO.produitId());
            } else {
                // Mettre à jour la quantité
                panierProduit.setQuantite(quantiteRestante);
                panierProduitRepository.save(panierProduit);
            }
        }
        panierRepository.save(panier);
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
    
    private CommandeProduit creerCommandeProduitDirect(Commande commande, Produit produit, int quantite) {
        CommandeProduit commandeProduit = new CommandeProduit();
        commandeProduit.setCommande(commande);
        commandeProduit.setProduit(produit);
        commandeProduit.setQuantite(quantite);
        commandeProduit.setPrixUnitaire(produit.getPrixUnitaire());
        return commandeProduit;
    }
    
    private Produit findProduitById(int id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable avec ID: " + id));
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
