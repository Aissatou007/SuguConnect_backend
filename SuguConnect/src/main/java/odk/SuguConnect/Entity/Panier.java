package odk.SuguConnect.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Panier {
    private int id ;
    @OneToOne
    private Consommateur consommateur;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "panier_id") ,
    inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List <Produit> produits ;
}
