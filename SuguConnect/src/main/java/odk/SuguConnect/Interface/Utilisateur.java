package odk.SuguConnect.Interface;

import jakarta.persistence.*;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutUtilisateur;

import java.time.LocalDate;
import java.util.Date;
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
    private long lattitude ;
    private Role role ;
    private String motDePasse ;
    private LocalDate dateInscription ;
    private StatutUtilisateur statutUtilisateur;
}
