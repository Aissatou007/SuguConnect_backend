package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.Conversation;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 1000)
    private String contenu;
    
    @Enumerated(EnumType.STRING)
    private TypeMessage typeMessage;
    
    private LocalDateTime dateEnvoi;
    
    private String cheminFichier; // Pour les images, vocaux et documents
    
    // Expéditeur du message (consommateur)
    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Consommateur expediteur;
    
    // Destinataire du message (producteur)
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Producteur destinataire;
    
    // Conversation à laquelle appartient le message
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    private boolean lu = false;
    
    @PrePersist
    protected void onCreate() {
        dateEnvoi = LocalDateTime.now();
    }
}