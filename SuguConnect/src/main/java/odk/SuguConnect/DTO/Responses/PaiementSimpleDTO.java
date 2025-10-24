package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutPaiement;

@Schema(description = "DTO simplifié pour un paiement")
public record PaiementSimpleDTO(
        @Schema(description = "ID du paiement", example = "1")
        int idPaiement,
        
        @Schema(description = "Montant du paiement", example = "12000.0")
        double montant,
        
        @Schema(description = "Date du paiement", example = "2025-10-23")
        java.time.LocalDate datePaiement,
        
        @Schema(description = "Méthode de paiement", example = "ORANGE_MONEY")
        ModePaiement methodePaiement,
        
        @Schema(description = "Statut du paiement", example = "INITIE")
        StatutPaiement statutPaiement
) {
}