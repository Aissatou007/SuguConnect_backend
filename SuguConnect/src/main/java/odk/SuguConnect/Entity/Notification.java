package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.TypeMessage;
import odk.SuguConnect.Interface.Utilisateur;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String titre;
    
    @Column(length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private TypeMessage typeMessage;
    
    private LocalDateTime dateEnvoi;
    
    private LocalDateTime dateExpiration;
    
    private boolean lu = false;
    
    // Action spécifique associée à la notification (URL, ID de commande, etc.)
    private String action;
    
    // Données supplémentaires au format JSON
    @Column(length = 2000)
    private String donneesSupplementaires;
    
    // Destinataire de la notification
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;
    
    // Méthode pour vérifier si la notification a expiré
    public boolean estExpiree() {
        if (dateExpiration == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(dateExpiration);
    }
}
