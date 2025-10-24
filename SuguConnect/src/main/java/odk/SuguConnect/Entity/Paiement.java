package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.StatutPaiement;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPaiement ;
    private double montant ;
    private LocalDate datePaiement;
    @Enumerated(EnumType.STRING)
    private ModePaiement methodePaiement ;
    private StatutPaiement statutPaiement ;
    @ManyToOne
    @JoinColumn(name = "consommateur")
    private Consommateur consommateur ;
    @OneToOne(mappedBy = "paiement")
    @JsonIgnore
    private Commande commande ;
}
