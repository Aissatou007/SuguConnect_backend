package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Interface.Utilisateur;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Consommateur extends Utilisateur {
   @OneToOne
    private Panier panier ;
   @OneToMany(mappedBy = "consommateur" , cascade = CascadeType.ALL)
    private List<Paiement> paiements ;
    @OneToMany(mappedBy = "consommateur" , cascade = CascadeType.ALL)
    private List<Commande> commandes;
  @ManyToMany
  @JoinTable(
          name = "favoris",
          joinColumns = @JoinColumn(name = "consommateur_id"),
          inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List<Produit> produit ;
}
