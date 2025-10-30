package odk.SuguConnect.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Classe_abstraite.Utilisateur;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Livreur extends Utilisateur {
    private String matricule;
    private String vehicule;
    private boolean disponible = true;
}