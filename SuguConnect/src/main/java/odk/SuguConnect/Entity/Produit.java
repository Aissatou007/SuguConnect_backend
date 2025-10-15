package odk.SuguConnect.Entity;

import jakarta.persistence.*;

import java.util.List;

public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String nom ;
    private int quantite ;
    private String description ;
    private float prixUnitaire ;
    private int stockDisponible ;
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    @ManyToMany
    private List<Panier> paniers ;
}
