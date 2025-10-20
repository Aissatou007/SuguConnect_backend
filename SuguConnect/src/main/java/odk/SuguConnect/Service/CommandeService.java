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

    public CommandeService(CommandeRepository commandeRepository, ProduitRepository produitRepository, ConsommateurRepository consommateurRepository, PaiementRepository paiementRepository, PanierRepository panierRepository) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
        this.consommateurRepository = consommateurRepository;
        this.paiementRepository = paiementRepository;
        this.panierRepository = panierRepository;
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
}
