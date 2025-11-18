package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Produit;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Consommateur participant à la conversation
    @ManyToOne
    @JoinColumn(name = "consommateur_id")
    private Consommateur consommateur;
    
    // Producteur participant à la conversation
    @ManyToOne
    @JoinColumn(name = "producteur_id")
    private Producteur producteur;
    
    // Produit concerné par la conversation
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    private LocalDateTime dateCreation;
    
    private LocalDateTime dateDernierMessage;
    
    private boolean active = true;
    
    // Messages de la conversation
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateDernierMessage = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateDernierMessage = LocalDateTime.now();
    }
}