package odk.SuguConnect.DTO.Responses;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VenteDTO {
    private String date;      // Date de la commande
    private String produit;   // Nom du produit
    private int quantite;     // Quantité vendue
    private float montant;    // Montant total (prixUnitaire * quantite)
}
