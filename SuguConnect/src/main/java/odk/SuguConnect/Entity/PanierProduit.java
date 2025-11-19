package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PanierProduit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "panier_id")
    @JsonBackReference
    private Panier panier;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    @JsonIgnoreProperties({"panier", "producteur", "categorie", "photos"})
    private Produit produit;
    
    private int quantite;
    private double prixUnitaire;
    private boolean dejaCommande;
}