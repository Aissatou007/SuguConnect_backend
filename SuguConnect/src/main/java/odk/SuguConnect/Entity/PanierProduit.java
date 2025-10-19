package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@NoArgsConstructor
public class PanierProduit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
    private int quantite;
    private double prixUnitaire;
    private boolean dejaCommande;

}
