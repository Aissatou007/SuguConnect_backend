package odk.SuguConnect.DTO.SimpleDTO;

import odk.SuguConnect.Enums.ModePaiement;

import java.time.LocalDate;

public record CreatePaiementRequest(
        Double montant,
        LocalDate datePaiement,
        ModePaiement methodePaiement,
        Integer consommateurId
) {
}
