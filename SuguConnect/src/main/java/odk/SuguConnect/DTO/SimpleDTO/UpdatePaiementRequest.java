package odk.SuguConnect.DTO.SimpleDTO;

import odk.SuguConnect.Enums.StatutPaiement;

public record UpdatePaiementRequest(
        StatutPaiement statutPaiement
) {
}
