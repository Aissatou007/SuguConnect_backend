package odk.SuguConnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @OneToOne
    private Consommateur consommateur;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "panier_id") ,
    inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List <Produit> produits ;
}
