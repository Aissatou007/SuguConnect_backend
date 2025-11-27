package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
        Commande commande = creerCommande(consommateur, request.getModePaiement());
        
        // Traiter les produits spécifiés
        double montantTotal = traiterProduitsSpecifiques(request.getProduits(), commande);
        commande.setMontantTotal(montantTotal);
        commandeRepository.save(commande);
        
        // Créer le paiement
        Paiement paiement = creerPaiement(commande, request.getModePaiement(), montantTotal);
        commande.setPaiement(paiement);
        commandeRepository.save(commande);
        
        // Envoyer les notifications
        envoyerNotificationsCommande(consommateur.getId(), commande, montantTotal);
        
        return commande;
    }
    

    public List<Commande> voirToutesLesCommandes() {
        return commandeRepository.findAll();
    }
    
    public List<Commande> voirCommandesParConsommateur(int idConsommateur) {
        Consommateur consommateur = findConsommateurById(idConsommateur);
        List<Commande> commandes = commandeRepository.findByConsommateur(consommateur);
        
        if (commandes.isEmpty()) {
            throw new EntityNotFoundException("Aucune commande trouvée pour ce consommateur");
        }
        return commandes;
    }
    
    public List<Commande> voirCommandesParProducteur(int producteurId, StatutCommande statut, String search) {
        List<Commande> commandes;
        
        // Filtrer par producteur et optionnellement par statut
        if (statut != null) {
            commandes = commandeRepository.findByProducteurIdAndStatut(producteurId, statut);
        } else {
            commandes = commandeRepository.findByProducteurId(producteurId);
        }
        
        // Inclure les commandes payées dans la liste des commandes en attente
        if (statut == StatutCommande.VALIDEE) {
            // Récupérer les commandes avec paiement validé
            List<Commande> commandesPayees = commandeRepository.findByProducteurIdAndPaiementStatutPaiement(producteurId, StatutPaiement.VALIDE);
            
            // Ajouter les commandes payées à la liste
            commandes.addAll(commandesPayees);
            
            // Supprimer les doublons et trier par ID ou date
            commandes = commandes.stream()
                .distinct()
                .sorted((c1, c2) -> Integer.compare(c2.getIdCommande(), c1.getIdCommande()))
                .toList();
        }
        
        // Filtrer par recherche si nécessaire
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            commandes = commandes.stream()
                .filter(c -> c.getIdCommande() != 0 && String.valueOf(c.getIdCommande()).contains(searchLower)
                          || c.getConsommateur().getNom().toLowerCase().contains(searchLower)
                          || c.getConsommateur().getPrenom().toLowerCase().contains(searchLower))
                .toList();
        }
        
        return commandes;
    }
    
    // Nouvelle méthode pour récupérer les commandes payées d'un producteur
    public List<Commande> voirCommandesPayeesParProducteur(int producteurId, String search) {
        List<Commande> commandes = commandeRepository.findByProducteurIdAndPaiementNotNull(producteurId);
        
        // Filtrer par recherche si nécessaire
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            commandes = commandes.stream()
                .filter(c -> c.getIdCommande() != 0 && String.valueOf(c.getIdCommande()).contains(searchLower)
                          || c.getConsommateur().getNom().toLowerCase().contains(searchLower)
                          || c.getConsommateur().getPrenom().toLowerCase().contains(searchLower))
                .toList();
        }
        
        return commandes;
    }
    
    @Transactional
    public Commande changerStatutCommande(int commandeId, int producteurId, StatutCommande nouveauStatut, String motifRejet) {
        System.out.println("=== Service changerStatutCommande ===");
        System.out.println("Commande ID: " + commandeId);
        System.out.println("Producteur ID: " + producteurId);
        System.out.println("Nouveau statut: " + nouveauStatut);
        System.out.println("Motif rejet: " + motifRejet);
        
        Commande commande = findCommandeById(commandeId);
        System.out.println("Commande trouvée: " + (commande != null ? commande.getIdCommande() : "null"));
        
        if (commande == null) {
            System.out.println("ERREUR: Commande non trouvée avec ID: " + commandeId);
            throw new EntityNotFoundException("Commande introuvable avec ID: " + commandeId);
        }
        
        verifierProprietaireCommande(commande, producteurId);
        System.out.println("Vérification propriétaire OK");
        
        // Mettre à jour le statut
        commande.setStatutCommande(nouveauStatut);
        if (nouveauStatut == StatutCommande.DECLINEE) {
            commande.setMotifRejet(motifRejet);
        }
        
        // Notifier selon le statut
        notifierChangementStatut(commande, nouveauStatut, motifRejet);
        System.out.println("Notification envoyée");
        
        Commande result = commandeRepository.save(commande);
        System.out.println("Commande sauvegardée avec succès");
        return result;
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
        
        // Si la commande est en EN_LIVRAISON, changer le statut à LIVREE lors de la validation
        if (commande.getStatutCommande() == StatutCommande.EN_LIVRAISON) {
            commande.setStatutCommande(StatutCommande.LIVREE);
            // Notifier le consommateur que la commande est livrée
            notifierChangementStatut(commande, StatutCommande.LIVREE, null);
        }
        
        // Notifier le producteur du revenu
        Producteur producteur = getProducteurCommande(commande);
        notificationService.notifierRevenuProducteur(
            producteur.getId(), 
            commandeId, 
            commande.getMontantTotal()
        );
        
        return commandeRepository.save(commande);
    }
    
    public Commande voirCommandeParId(int commandeId) {
        return findCommandeById(commandeId);
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
    
    /**
     * Traiter les produits spécifiés dans la requête de commande
     */
    private double traiterProduitsSpecifiques(List<ProduitCommandeDTO> produits, Commande commande) {
        double total = 0.0;
        
        for (ProduitCommandeDTO produitDTO : produits) {
            Produit produit = findProduitById(produitDTO.getProduitId());
            int quantite = produitDTO.getQuantite();
            
            // Vérifier et réduire le stock
            verifierEtReduireStock(produit, quantite);
            
            // Créer l'article de commande
            CommandeProduit commandeProduit = creerCommandeProduitDirect(commande, produit, quantite);
            commande.getCommandeProduits().add(commandeProduit);
            
            total += produit.getPrixUnitaire() * quantite;
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
        System.out.println("=== Vérification propriétaire commande ===");
        System.out.println("Commande ID: " + commande.getIdCommande());
        System.out.println("Producteur ID attendu: " + producteurId);
        
        Producteur producteur = getProducteurCommande(commande);
        System.out.println("Producteur réel de la commande: " + (producteur != null ? producteur.getId() : "null"));
        
        if (producteur == null) {
            System.out.println("ERREUR: Producteur non trouvé pour la commande " + commande.getIdCommande());
            throw new SecurityException("Producteur non trouvé pour cette commande");
        }
        
        if (producteur.getId() != producteurId) {
            System.out.println("ERREUR: Producteur " + producteur.getId() + " tente d'accéder à la commande du producteur " + producteurId);
            throw new SecurityException("Vous n'êtes pas autorisé à modifier cette commande. Producteur ID: " + producteur.getId() + ", Commande pour producteur ID: " + producteurId);
        }
        
        System.out.println("Vérification propriétaire OK");
    }
    
    private void notifierChangementStatut(Commande commande, StatutCommande nouveauStatut, String motifRejet) {
        int consommateurId = commande.getConsommateur().getId();
        int commandeId = commande.getIdCommande();
        
        switch (nouveauStatut) {
            case DECLINEE:
                notificationService.notifierCommandeRefusee(consommateurId, commandeId, motifRejet);
                break;
            case VALIDEE:
                notificationService.notifierCommandeValidee(consommateurId, commandeId);
                break;
            case EN_LIVRAISON:
                notificationService.notifierCommandeEnLivraison(consommateurId, commandeId);
                break;
            case LIVREE:
                notificationService.notifierCommandeLivree(consommateurId, commandeId);
                break;
        }
    }
    
    private void verifierProprietaireReception(Commande commande, int consommateurId) {
        if (commande.getConsommateur().getId() != consommateurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à valider cette commande");
        }
    }
    
    private void verifierStatutLivraison(Commande commande) {
        // Permettre la validation de réception pour les commandes EN_LIVRAISON ou LIVREE
        if (commande.getStatutCommande() != StatutCommande.EN_LIVRAISON && 
            commande.getStatutCommande() != StatutCommande.LIVREE) {
            throw new IllegalStateException("Vous ne pouvez valider que les commandes en cours de livraison ou déjà livrées");
        }
    }
    
    private void verifierNonValidee(Commande commande) {
        if (commande.isReceptionValidee()) {
            throw new IllegalStateException("Cette commande a déjà été validée");
        }
    }
}
