package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutPaiement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO simplifié pour un paiement")
public class PaiementSimpleDTO {
    @Schema(description = "ID du paiement", example = "1")
    private int idPaiement;
    
    @Schema(description = "Montant du paiement", example = "12000.0")
    private double montant;
    
    @Schema(description = "Date du paiement", example = "2025-10-23")
    private java.time.LocalDate datePaiement;
    
    @Schema(description = "Méthode de paiement", example = "ORANGE_MONEY")
    private ModePaiement methodePaiement;
    
    @Schema(description = "Statut du paiement", example = "INITIE")
    private StatutPaiement statutPaiement;
}