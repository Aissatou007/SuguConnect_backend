package odk.SuguConnect.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"consommateur", "produits", "panierProduits"})
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @OneToOne
    private Consommateur consommateur;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "panier_id") ,
    inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List <Produit> produits = new ArrayList<>();
    @OneToMany(mappedBy = "panier" , cascade = CascadeType.ALL)
    private List<PanierProduit> panierProduits = new ArrayList<>();
}
