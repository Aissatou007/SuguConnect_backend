package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private List<CommandeProduit> commandeProduits = new ArrayList<>();
    private Double montantTotal;
    private StatutCommande statutCommande;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;
    private LocalDate dateCommande;
    private String motifRejet;
    
    // Validation de réception par le consommateur
    private boolean receptionValidee = false;
    private LocalDate dateReceptionValidee;
    
    @ManyToOne
    @JoinColumn(name = "consommateur")
    private Consommateur consommateur;
    @OneToOne
    @JoinColumn(name ="paiement_id")
    private Paiement paiement;
    
    @OneToOne(mappedBy = "commande")
    private Avis avis;
}
