package odk.SuguConnect.Entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

public class Panier {
    private int id ;
    @OneToOne
    private Consommateur consommateur;
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "panier_id") ,
    inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List <Produit> produits ;
}
