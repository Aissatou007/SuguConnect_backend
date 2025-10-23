package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;
    
    @ManyToOne
    @JoinColumn(name = "consommateur_id")
    private Consommateur consommateur;
    
    @ManyToOne
    @JoinColumn(name = "producteur_id")
    private Producteur producteur;
    
    private int note;
    
    @Column(length = 1000)
    private String commentaire;
    
    private LocalDateTime dateAvis;
    
    private boolean valide = false;
}
