package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Config.StatutCommandeConverter;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutCommande;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCommande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits = new ArrayList<>();

    private Double montantTotal;
    private Double prixLivraison;

    @Convert(converter = StatutCommandeConverter.class)
    @Column(columnDefinition = "VARCHAR(50)")
    private StatutCommande statutCommande;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    private LocalDate dateCommande;
    private String motifRejet;

    private boolean receptionValidee = false;
    private LocalDate dateReceptionValidee;

    @ManyToOne
    @JoinColumn(name = "consommateur_id")
    @JsonIgnoreProperties({"commandes", "avis", "paiements"}) // Évite la boucle sans cacher le consommateur
    private Consommateur consommateur;

    @OneToOne
    @JoinColumn(name ="paiement_id")
    @JsonIgnoreProperties({"commande"}) // Évite la boucle
    private Paiement paiement;

    @OneToOne(mappedBy = "commande")
    @JsonIgnore
    private Avis avis;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    @JsonIgnore
    private Livreur livreurPrefere;

    @Override
    public String toString() {
        return "Commande{id=" + idCommande + ", montant=" + montantTotal + "}";
    }
}
