package odk.SuguConnect.Entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import odk.SuguConnect.Classe_abstraite.Utilisateur;

@Entity
@Getter
@Setter
public class Admin extends Utilisateur {

}
