package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.PasserCommandePanierDTO;
import odk.SuguConnect.DTO.Request.ProduitCommandeDTO;
import odk.SuguConnect.DTO.Responses.HistoriqueVenteDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Repository.*;
import odk.SuguConnect.Entity.Livreur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final LivreurRepository livreurRepository;

    @Transactional
    public Commande passerCommande(int idConsommateur, PasserCommandePanierDTO request) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        Panier panier = validerPanier(consommateur);
        
        // Create the order
        Commande commande = creerCommande(consommateur, request.modePaiement());
        
        // Process specified products from cart
        double montantTotal = traiterProduitsSpecifiquesDuPanier(request.produits(), commande, panier);
        commande.setMontantTotal(montantTotal);
        commandeRepository.save(commande);
        
        // Create and process payment based on payment method
        Paiement paiement = creerEtTraiterPaiement(commande, request.modePaiement(), montantTotal, request.numeroTelephone());
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        
        // Send notifications
        envoyerNotificationsCommande(consommateur.getId(), commande, montantTotal);
        
        // Remove ordered products from cart
        retirerProduitsDuPanier(panier, request.produits());
        
        return commande;
    }
    
    /**
     * Passer une commande avec des produits spécifiques (sans utiliser le panier)
     * Permet au consommateur de choisir directement les produits à commander
     */
    @Transactional(readOnly = true)
    public List<Commande> getCommandesParProducteur(int producteurId) {
        // Utiliser la méthode optimisée avec JOIN FETCH pour charger toutes les relations
        // Cela évite les problèmes de lazy loading et garantit que toutes les données sont chargées
        return commandeRepository.findByProducteurWithRelations(producteurId);
    }
    public int countCommandesParConsommateur(Long consommateurId) {
        return commandeRepository.countByConsommateurId(consommateurId); // méthode JPA
    }
    @Transactional(readOnly = true)
    public List<Commande> voirCommandesParConsommateur(int idConsommateur) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        
        // Utiliser la méthode avec JOIN FETCH pour charger toutes les relations
        // Cela évite les problèmes de lazy loading et garantit que toutes les données sont chargées
        List<Commande> commandes = commandeRepository.findByConsommateurWithRelations(consommateur);
        
        // Retourner une liste vide au lieu de lancer une exception si aucune commande
        // Cela permet de gérer le cas où le consommateur n'a pas encore passé de commande
        return commandes;
    }
    
    public Commande voirCommandeParId(int commandeId) {
        return findCommandeById(commandeId);
    }
    
    public List<Commande> voirToutesLesCommandes() {
        return commandeRepository.findAll();
    }
    public List<HistoriqueVenteDTO> getHistoriqueVentesProduit(int produitId) {
        return commandeRepository.findAll().stream()
                .flatMap(commande -> commande.getCommandeProduits().stream()
                        .filter(cp -> cp.getProduit().getId() == produitId)
                        .map(cp -> {
                            HistoriqueVenteDTO dto = new HistoriqueVenteDTO();
                            dto.setNomConsommateur(commande.getConsommateur().getPrenom() + " " + commande.getConsommateur().getNom());
                            dto.setQuantite(cp.getQuantite());
                            dto.setMontant(cp.getPrixUnitaire() * cp.getQuantite());
                            dto.setDateCommande(commande.getDateCommande());
                            return dto;
                        }))
                .toList();
    }
    @Transactional
    public Commande changerStatutCommande(int commandeId, int producteurId, StatutCommande nouveauStatut, String motifRejet, Integer livreurId, Double prixLivraison) {
        Commande commande = findCommandeById(commandeId);
        verifierProprietaireCommande(commande, producteurId);
        
        // Mettre à jour le statut
        commande.setStatutCommande(nouveauStatut);
        if (nouveauStatut == StatutCommande.DECLINEE || nouveauStatut == StatutCommande.REFUSEE) {
            commande.setMotifRejet(motifRejet);
        }
        
        // Si le statut est LIVREE et qu'un livreur est fourni, l'assigner
        if (nouveauStatut == StatutCommande.LIVREE && livreurId != null) {
            Livreur livreur = livreurRepository.findById(livreurId)
                    .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
            
            if (!livreur.isDisponible()) {
                throw new IllegalArgumentException("Le livreur sélectionné n'est pas disponible");
            }
            
            commande.setLivreurPrefere(livreur);
            
            // Définir le prix de livraison
            if (prixLivraison == null || prixLivraison <= 0) {
                prixLivraison = Math.max(commande.getMontantTotal() * 0.10, 500.0);
            }
            commande.setPrixLivraison(prixLivraison);
            
            // Envoyer la notification au consommateur
            notifierLivraisonAssignee(commande, livreur, prixLivraison);
        } else {
            // Notifier selon le statut (sauf si LIVREE avec livreur, déjà notifié)
            notifierChangementStatut(commande, nouveauStatut, motifRejet);
        }
        
        return commandeRepository.save(commande);
    }

    @Transactional
    public Commande assignerLivreurACommande(int commandeId, int producteurId, int livreurId, Double prixLivraison) {
        Commande commande = findCommandeById(commandeId);
        verifierProprietaireCommande(commande, producteurId);
        
        // Vérifier que le livreur existe et est disponible
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new EntityNotFoundException("Livreur non trouvé"));
        
        if (!livreur.isDisponible()) {
            throw new IllegalArgumentException("Le livreur sélectionné n'est pas disponible");
        }
        
        // Assigner le livreur à la commande
        commande.setLivreurPrefere(livreur);
        
        // Définir le prix de livraison (utiliser une valeur par défaut si non fourni)
        if (prixLivraison == null || prixLivraison <= 0) {
            // Prix par défaut : 10% du montant total ou minimum 500 FCFA
            prixLivraison = Math.max(commande.getMontantTotal() * 0.10, 500.0);
        }
        commande.setPrixLivraison(prixLivraison);
        
        // Envoyer la notification au consommateur
        notifierLivraisonAssignee(commande, livreur, prixLivraison);
        
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
    
    private PanierProduit trouverProduitDansPanier(Panier panier, int produitId) {
        return panier.getPanierProduits().stream()
                .filter(pp -> pp.getProduit().getId() == produitId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable dans le panier"));
    }
    
    private Paiement creerEtTraiterPaiement(Commande commande, ModePaiement modePaiement, double montant, String numeroTelephone) {
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMethodePaiement(modePaiement);
        paiement.setMontant(montant);
        paiement.setDatePaiement(LocalDate.now());
        paiement.setConsommateur(commande.getConsommateur());
        
        // Traiter le paiement selon le mode choisi
        switch (modePaiement) {
            case ESPECES -> {
                // Pour les paiements en espèces, on valide directement
                paiement.setStatutPaiement(StatutPaiement.VALIDE);
            }
            case ORANGE_MONEY, WAVE, MOBILE_MONEY -> {
                // Pour les paiements mobiles, on initie le paiement
                if (numeroTelephone == null || numeroTelephone.trim().isEmpty()) {
                    throw new IllegalArgumentException("Le numéro de téléphone est requis pour les paiements mobiles");
                }
                paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
                // TODO: Intégration avec l'API Orange Money/Wave pour initier le paiement
                // Pour l'instant, on crée juste le paiement en attente
            }
            default -> {
                paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);
            }
        }
        
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
    
    private void retirerProduitsDuPanier(Panier panier, List<ProduitCommandeDTO> produits) {
        for (ProduitCommandeDTO produitDTO : produits) {
            PanierProduit panierProduit = trouverProduitDansPanier(panier, produitDTO.produitId());
            int quantiteRestante = panierProduit.getQuantite() - produitDTO.quantite();
            
            if (quantiteRestante <= 0) {
                panier.getPanierProduits().remove(panierProduit);
                panierProduitRepository.delete(panierProduit);
                panier.getProduits().removeIf(p -> p.getId() == produitDTO.produitId());
            } else {
                panierProduit.setQuantite(quantiteRestante);
                panierProduitRepository.save(panierProduit);
            }
        }
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
            case DECLINEE, REFUSEE -> notificationService.notifierCommandeRefusee(consommateurId, commandeId, motifRejet);
            case VALIDEE -> notificationService.notifierCommandeValidee(consommateurId, commandeId);
            case EN_LIVRAISON -> notificationService.notifierCommandeEnLivraison(consommateurId, commandeId);
            case LIVREE -> {
                // Pour LIVREE, la notification sera envoyée lors de l'assignation du livreur
                // On ne notifie pas ici si un livreur n'est pas encore assigné
                if (commande.getLivreurPrefere() != null && commande.getPrixLivraison() != null) {
                    notifierLivraisonAssignee(commande, commande.getLivreurPrefere(), commande.getPrixLivraison());
                }
            }
            default -> {
                // Pas de notification pour les autres statuts
            }
        }
    }
    
    private void notifierLivraisonAssignee(Commande commande, Livreur livreur, Double prixLivraison) {
        int consommateurId = commande.getConsommateur().getId();
        int commandeId = commande.getIdCommande();
        String nomLivreur = livreur.getPrenom() + " " + livreur.getNom();
        String dateCommande = commande.getDateCommande().toString();
        
        String message = String.format(
            "Commande #%d, faite le %s, sera livrée par %s et prix de la livraison est : %.2f FCFA",
            commandeId, dateCommande, nomLivreur, prixLivraison
        );
        
        String action = "/commandes/" + commandeId;
        notificationService.creerNotification(
            consommateurId,
            TypeMessage.COMMANDE_EN_LIVRAISON,
            message,
            action,
            168
        );
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
