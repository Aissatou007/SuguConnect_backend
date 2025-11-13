package odk.SuguConnect.DTO.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueVenteDTO {
    private String nomConsommateur;
    private int quantite;
    private double montant;
    private LocalDate dateCommande;    // Montant total (prixUnitaire * quantite)
}
