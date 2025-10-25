package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.ModeLivraison;
import odk.SuguConnect.Enums.StatutLivraison;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private ModeLivraison modeLivraison;
    private StatutLivraison statutLivraison;
    
    @OneToOne
    private Commande commande;
    
    @ManyToOne
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;
}