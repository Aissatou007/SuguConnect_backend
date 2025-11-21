package odk.SuguConnect.DTO.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockProduitResponseDTO {
    private int id;
    private String nom;
    private String description;
    private double prix;
    private int quantite;
    private int seuilAlerte;
    private String unite;
    private String categorie;
    private boolean bio;
    private String imageUrl;
    private LocalDate dateCreation;
    private LocalDate dateMiseAJour;
    private String statut; // "en_stock", "stock_faible", "epuise"
    private double valeurTotale;
}