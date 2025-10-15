package odk.SuguConnect.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import odk.SuguConnect.Interface.Utilisateur;

import java.util.List;

@Entity
public class Producteur extends Utilisateur {
    private String desription ;
   private List<Categorie> categories;

}
