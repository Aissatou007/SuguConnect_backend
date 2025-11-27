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
@JsonIgnoreProperties({"paniers", "consommateurs", "commandeProduitList", "panierProduits"})
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
    @Column(name = "est_bio", nullable = false)
    private boolean estBio = false;
    @Column(name = "seuil_alerte", nullable = false)
    private int seuilAlerte = 10; // Valeur par défaut : alerte quand stock < 10
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "produit_photos", joinColumns = @JoinColumn(name = "produit_id"))
    @Column(name = "photo_url", length = 500)
    private List<String> photos = new ArrayList<>();
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
    @OneToMany(mappedBy = "produit" , cascade = CascadeType.ALL)
    private List<PanierProduit> panierProduits ;
}
