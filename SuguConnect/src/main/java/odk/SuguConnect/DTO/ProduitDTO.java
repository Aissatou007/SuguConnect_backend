package odk.SuguConnect.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.Unite;

/**
 * DTO pour représenter un produit avec toutes les informations nécessaires
 */
@Getter
@Setter
@NoArgsConstructor
public class ProduitDTO {
    private int produitId;
    private String nomProduit;
    private String description;
    private float prixUnitaire;
    private Unite unite;
    private String photoUrl;
    private int stockDisponible;
    private int producteurId;
    private String nomProducteur;
    private int categorieId;
    private String libelleCategorie;
}