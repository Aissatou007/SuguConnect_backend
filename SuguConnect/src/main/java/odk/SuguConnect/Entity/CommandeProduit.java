package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "commande_produit", uniqueConstraints = @UniqueConstraint(columnNames = {"commande_id", "produit_id"}))
public class CommandeProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    @JsonBackReference //  évite la boucle Commande ↔ CommandeProduit
    private Commande commande;

    private int quantite;
    private float prixUnitaire;

    @Override
    public String toString() {
        return "CommandeProduit{id=" + id + ", quantite=" + quantite + "}";
    }
}
