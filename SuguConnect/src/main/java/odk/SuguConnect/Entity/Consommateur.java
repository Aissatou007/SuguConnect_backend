package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Classe_abstraite.Utilisateur;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"panier", "paiements", "commandes", "produit", "motDePasse"})
public class Consommateur extends Utilisateur {
   @OneToOne(cascade = CascadeType.ALL)
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
