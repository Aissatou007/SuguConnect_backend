package odk.SuguConnect.DTO.SimpleDTO;

import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutPaiement;

import java.time.LocalDate;

public record PaiementRecord(
        Integer idPaiement,
        Double montant,
        LocalDate datePaiement,
        ModePaiement methodePaiement,
        StatutPaiement statutPaiement,
        Integer consommateurId
) {
}
