package odk.SuguConnect.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Interface.Utilisateur;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producteur extends Utilisateur {
    private StatutProducteur statutProducteur;
    private String desription ;
    @OneToMany(mappedBy = "producteur" , cascade = CascadeType.ALL)
   private List<Produit> produits;

}
