package odk.SuguConnect.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import odk.SuguConnect.Enums.Mode;
import odk.SuguConnect.Enums.StatutLivraison;

@Entity
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private Mode mode ;
    private StatutLivraison statutLivraison ;
}
