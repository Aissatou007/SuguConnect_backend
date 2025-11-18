package odk.SuguConnect.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import odk.SuguConnect.Enums.Unite;

/**
 * DTO pour représenter un produit populaire avec son nombre de commandes
 */
@Getter
@Setter
@NoArgsConstructor
public class ProduitPopulaireDTO {
    private int produitId;
    private String nomProduit;
    private String description;
    private float prixUnitaire;
    private String unite;
    private String photoUrl;
    private int nombreCommandes;
    private int producteurId;
    private String nomProducteur;
    private int categorieId;
    private String libelleCategorie;
    
    // Constructeur pour la requête JPQL
    public ProduitPopulaireDTO(int produitId, String nomProduit, String description, 
                              float prixUnitaire, Unite unite, String photoUrl,
                              long nombreCommandes, int producteurId, String nomProducteur,
                              int categorieId, String libelleCategorie) {
        this.produitId = produitId;
        this.nomProduit = nomProduit;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite != null ? unite.name() : ""; // Conversion de l'enum en string
        this.photoUrl = photoUrl;
        this.nombreCommandes = (int) nombreCommandes; // Conversion de long vers int
        this.producteurId = producteurId;
        this.nomProducteur = nomProducteur;
        this.categorieId = categorieId;
        this.libelleCategorie = libelleCategorie;
    }
}