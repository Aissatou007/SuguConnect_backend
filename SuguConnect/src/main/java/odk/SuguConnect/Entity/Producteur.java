package odk.SuguConnect.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Classe_abstraite.Utilisateur;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"produits", "motDePasse", "conversations"})
public class Producteur extends Utilisateur {
    @Enumerated(EnumType.STRING)
    private StatutProducteur statutProducteur;
    private String description;
    private String nomFerme;
    private String photoUrl;
    @OneToMany(mappedBy = "producteur", cascade = CascadeType.ALL)
    private List<Produit> produits;
    
    // Conversations du producteur
    @OneToMany(mappedBy = "producteur", cascade = CascadeType.ALL)
    private List<Conversation> conversations;
}