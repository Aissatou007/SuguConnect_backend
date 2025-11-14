package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de réponse pour une commande")
public class CommandeResponseDTO {
    @Schema(description = "ID de la commande", example = "1")
    private int idCommande;
    
    @Schema(description = "Montant total de la commande", example = "12000.0")
    private Double montantTotal;
    
    @Schema(description = "Statut de la commande", example = "EN_ATTENTE")
    private StatutCommande statutCommande;
    
    @Schema(description = "Mode de paiement choisi", example = "ORANGE_MONEY")
    private ModePaiement modePaiement;
    
    @Schema(description = "Date de passage de la commande", example = "2025-10-23")
    private LocalDate dateCommande;
    
    @Schema(description = "Motif de rejet si la commande est refusée", example = "Stock insuffisant")
    private String motifRejet;
    
    @Schema(description = "Indique si la réception a été validée par le consommateur")
    private boolean receptionValidee;
    
    @Schema(description = "Date de validation de la réception", example = "2025-10-25")
    private LocalDate dateReceptionValidee;
    
    @Schema(description = "Produits commandés")
    private List<CommandeProduitResponseDTO> commandeProduits;
    
    @Schema(description = "Informations du consommateur (sans relations)")
    private ConsommateurSimpleDTO consommateur;
    
    @Schema(description = "Informations du paiement (sans relations)")
    private PaiementSimpleDTO paiement;
}