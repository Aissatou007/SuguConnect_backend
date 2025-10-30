package odk.SuguConnect.Classe_abstraite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.Role;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String prenom;
    private String telephone ;
    private String email ;
    private String localisation ;
    private long longitude ;
    @Column(name = "lattitude")
    private long latitude ;
    @Enumerated(EnumType.STRING)
    private Role role ;
    private String motDePasse ;
    private LocalDate dateInscription ;
    private String MotifDeRejet;
    private boolean actif;


}
