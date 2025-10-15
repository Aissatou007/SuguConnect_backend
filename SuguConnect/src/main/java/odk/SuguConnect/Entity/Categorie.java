package odk.SuguConnect.Entity;

import jakarta.persistence.*;

import java.util.List;

public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String libelle;
    @OneToMany(mappedBy = "categorie" , cascade = CascadeType.ALL)
    private List<Produit> produits ;

}
