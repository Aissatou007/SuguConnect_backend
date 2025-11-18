package odk.SuguConnect.DTO;

import lombok.Getter;
import lombok.Setter;
import odk.SuguConnect.Enums.Unite;

import java.util.List;

/**
 * DTO pour représenter les détails complets d'un produit
 */
@Getter
@Setter
public class ProduitDetailDTO {
    private int id;
    private String nom;
    private String description;
    private float prixUnitaire;
    private Unite unite;
    private int stockDisponible;
    private int quantite;
    private List<String> photos;
    private int producteurId;
    private String nomProducteur;
    private String prenomProducteur;
    private String telephoneProducteur;
    private String emailProducteur;
    private String localisationProducteur;
    private int categorieId;
    private String libelleCategorie;
    private int noteMoyenne; // Note moyenne sur 5
    private int nombreEvaluations; // Nombre total d'évaluations
    
    // Constructeur par défaut généré par Lombok
    
    // Constructeur pour la conversion depuis l'entité Produit
    public ProduitDetailDTO(int id, String nom, String description, float prixUnitaire, 
                           Unite unite, int stockDisponible, int quantite, List<String> photos,
                           int producteurId, String nomProducteur, String prenomProducteur, 
                           String telephoneProducteur, String emailProducteur, String localisationProducteur,
                           int categorieId, String libelleCategorie, int noteMoyenne, int nombreEvaluations) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prixUnitaire = prixUnitaire;
        this.unite = unite;
        this.stockDisponible = stockDisponible;
        this.quantite = quantite;
        this.photos = photos;
        this.producteurId = producteurId;
        this.nomProducteur = nomProducteur;
        this.prenomProducteur = prenomProducteur;
        this.telephoneProducteur = telephoneProducteur;
        this.emailProducteur = emailProducteur;
        this.localisationProducteur = localisationProducteur;
        this.categorieId = categorieId;
        this.libelleCategorie = libelleCategorie;
        this.noteMoyenne = noteMoyenne;
        this.nombreEvaluations = nombreEvaluations;
    }
}