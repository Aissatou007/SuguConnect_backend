package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.Unite;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"paniers", "consommateurs", "commandeProduitList", "panierProduits", "producteur", "hibernateLazyInitializer", "handler"})
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
    @ElementCollection
    @CollectionTable(name = "produit_photos", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "producteur_id")
    @JsonIgnoreProperties({"produits", "panier", "commandes", "avisRecus", "avisDonnes"})
    private Producteur producteur;
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    @ManyToMany
    @JsonIgnoreProperties({"produits", "consommateur"})
    private List<Panier> paniers ;
    @OneToMany(mappedBy = "produit")
    @JsonIgnoreProperties({"produit"})
    private List<CommandeProduit>commandeProduitList ;
    @ManyToMany(mappedBy = "produit")
    @JsonIgnoreProperties({"produits"})
    private List<Consommateur> consommateurs;
    @OneToMany(mappedBy = "produit" , cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"produit"})
    private List<PanierProduit> panierProduits ;
}