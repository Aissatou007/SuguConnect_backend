package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.Unite;

import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String nom ;
    private int quantite ;
    private String description ;
    private float prixUnitaire ;
    private Unite unite ;
    private int stockDisponible ;
    @ManyToOne
    @JoinColumn(name = "producteur_id")
    private Producteur producteur;
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    @ManyToMany
    private List<Panier> paniers ;
    @OneToMany(mappedBy = "produit")
    private List<CommandeProduit>commandeProduitList ;
    @ManyToMany(mappedBy = "produit")
    private List<Consommateur> consommateurs;
}
