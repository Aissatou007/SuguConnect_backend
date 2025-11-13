package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Responses.*;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.CommandeProduit;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Entity.Produit;

import java.util.stream.Collectors;

public class CommandeMapper {
    
    public static CommandeResponseDTO toResponse(Commande commande) {
        if (commande == null) {
            return null;
        }
        
        return new CommandeResponseDTO(
                commande.getIdCommande(),
                commande.getMontantTotal(),
                commande.getStatutCommande(),
                commande.getModePaiement(),
                commande.getDateCommande(),
                commande.getMotifRejet(),
                commande.isReceptionValidee(),
                commande.getDateReceptionValidee(),
                commande.getCommandeProduits().stream()
                        .map(CommandeMapper::toCommandeProduitResponse)
                        .collect(Collectors.toList()),
                toConsommateurSimple(commande.getConsommateur()),
                toPaiementSimple(commande.getPaiement())
        );
    }
    
    private static CommandeProduitResponseDTO toCommandeProduitResponse(CommandeProduit commandeProduit) {
        if (commandeProduit == null) {
            return null;
        }
        
        return new CommandeProduitResponseDTO(
                commandeProduit.getId(),
                toProduitSimple(commandeProduit.getProduit()),
                commandeProduit.getQuantite(),
                commandeProduit.getPrixUnitaire()
        );
    }
    
    private static ProduitSimpleDTO toProduitSimple(Produit produit) {
        if (produit == null) {
            return null;
        }
        
        Integer producteurId = produit.getProducteur() != null ? produit.getProducteur().getId() : null;
        
        return new ProduitSimpleDTO(
                produit.getId(),
                produit.getNom(),
                produit.getPrixUnitaire(),
                produit.getProducteur() != null ? produit.getProducteur().getId() : null,
                produit.getProducteur() != null ? produit.getProducteur().getNom() : "Inconnu",
                produit.getProducteur() != null ? produit.getProducteur().getPrenom() : "Inconnu"
        );
    }
    
    private static ConsommateurSimpleDTO toConsommateurSimple(Consommateur consommateur) {
        if (consommateur == null) {
            return null;
        }
        
        return new ConsommateurSimpleDTO(
                consommateur.getId(),
                consommateur.getNom(),
                consommateur.getPrenom(),
                consommateur.getTelephone(),
                consommateur.getEmail(),
                consommateur.getLocalisation(),
                consommateur.getLongitude(),
                consommateur.getLatitude(),
                consommateur.getRole()
        );
    }
    
    private static PaiementSimpleDTO toPaiementSimple(Paiement paiement) {
        if (paiement == null) {
            return null;
        }
        
        return new PaiementSimpleDTO(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getMethodePaiement(),
                paiement.getStatutPaiement()
        );
    }
}