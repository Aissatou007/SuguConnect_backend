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
        
        // Create payment
        Paiement paiement = creerPaiement(commande, request.modePaiement(), montantTotal);
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
    public List<Commande> getCommandesParProducteur(int producteurId) {
        // Récupérer toutes les commandes contenant au moins un produit du producteur
        return commandeRepository.findAll().stream()
                .filter(cmd -> cmd.getCommandeProduits().stream()
                        .anyMatch(cp -> cp.getProduit().getProducteur().getId() == producteurId))
                .toList();
    }
    
    /**
     * Récupère toutes les commandes pour un producteur spécifique avec des filtres optionnels
     */
    public List<Commande> getCommandesParProducteur(int producteurId, StatutCommande statut) {
        return commandeRepository.findAll().stream()
                .filter(cmd -> cmd.getCommandeProduits().stream()
                        .anyMatch(cp -> cp.getProduit().getProducteur().getId() == producteurId))
                .filter(cmd -> statut == null || cmd.getStatutCommande() == statut)
                .toList();
    }
    
    public int countCommandesParConsommateur(Long consommateurId) {
        return commandeRepository.countByConsommateurId(consommateurId); // méthode JPA
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
        if (panier == null) {
            throw new EntityNotFoundException("Panier non trouvé. Veuillez d'abord créer un panier.");
        }
        if (panier.getPanierProduits() == null || panier.getPanierProduits().isEmpty()) {
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
        
        // Validate input
        if (produits == null || produits.isEmpty()) {
            throw new IllegalArgumentException("La liste des produits à commander ne peut pas être vide");
        }
        
        if (panier.getPanierProduits() == null || panier.getPanierProduits().isEmpty()) {
            throw new EntityNotFoundException("Le panier est vide. Veuillez ajouter des produits avant de passer une commande.");
        }
        
        for (ProduitCommandeDTO produitDTO : produits) {
            // Validate product DTO
            if (produitDTO == null) {
                throw new IllegalArgumentException("Un des produits dans la liste est invalide");
            }
            
            if (produitDTO.quantite() <= 0) {
                throw new IllegalArgumentException(
                    String.format("La quantité pour le produit ID %d doit être supérieure à zéro", produitDTO.produitId()));
            }
            
            // Trouver le produit dans le panier
            PanierProduit panierProduit = trouverProduitDansPanier(panier, produitDTO.produitId());
            int quantite = produitDTO.quantite();
            
            // Vérifier que la quantité demandée est disponible dans le panier
            if (quantite > panierProduit.getQuantite()) {
                throw new IllegalArgumentException(
                    String.format("Quantité demandée (%d) supérieure à la quantité dans le panier (%d) pour le produit %s",
                        quantite, panierProduit.getQuantite(), 
                        panierProduit.getProduit() != null ? panierProduit.getProduit().getNom() : "Inconnu")
                );
            }
            
            // Vérifier et réduire le stock
            Produit produit = panierProduit.getProduit();
            if (produit == null) {
                throw new EntityNotFoundException("Produit introuvable dans le panier");
            }
            
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
        if (produit == null) {
            throw new EntityNotFoundException("Produit introuvable");
        }
        
        if (produit.getStockDisponible() < quantite) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant pour %s. Disponible: %d, Demandé: %d",
                    produit.getNom() != null ? produit.getNom() : "Produit inconnu", 
                    produit.getStockDisponible(), quantite)
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
        if (panier.getPanierProduits() == null || panier.getPanierProduits().isEmpty()) {
            throw new EntityNotFoundException("Aucun produit trouvé dans le panier");
        }
        
        return panier.getPanierProduits().stream()
                .filter(pp -> pp.getProduit() != null && pp.getProduit().getId() == produitId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Produit avec ID %d introuvable dans le panier. Veuillez vérifier que le produit est bien ajouté au panier.", produitId)));
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
