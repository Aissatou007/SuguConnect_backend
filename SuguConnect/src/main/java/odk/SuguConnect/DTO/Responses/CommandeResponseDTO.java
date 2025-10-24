package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO de réponse pour une commande")
public record CommandeResponseDTO(
        @Schema(description = "ID de la commande", example = "1")
        int idCommande,
        
        @Schema(description = "Montant total de la commande", example = "12000.0")
        Double montantTotal,
        
        @Schema(description = "Statut de la commande", example = "EN_ATTENTE")
        StatutCommande statutCommande,
        
        @Schema(description = "Mode de paiement choisi", example = "ORANGE_MONEY")
        ModePaiement modePaiement,
        
        @Schema(description = "Date de passage de la commande", example = "2025-10-23")
        LocalDate dateCommande,
        
        @Schema(description = "Motif de rejet si la commande est refusée", example = "Stock insuffisant")
        String motifRejet,
        
        @Schema(description = "Indique si la réception a été validée par le consommateur")
        boolean receptionValidee,
        
        @Schema(description = "Date de validation de la réception", example = "2025-10-25")
        LocalDate dateReceptionValidee,
        
        @Schema(description = "Produits commandés")
        List<CommandeProduitResponseDTO> commandeProduits,
        
        @Schema(description = "Informations du consommateur (sans relations)")
        ConsommateurSimpleDTO consommateur,
        
        @Schema(description = "Informations du paiement (sans relations)")
        PaiementSimpleDTO paiement
) {
}