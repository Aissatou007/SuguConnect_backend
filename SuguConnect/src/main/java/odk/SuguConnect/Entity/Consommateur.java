package odk.SuguConnect.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import odk.SuguConnect.Interface.Utilisateur;

@Entity
public class Consommateur extends Utilisateur {
   @OneToOne
    private Panier panier ;
}
