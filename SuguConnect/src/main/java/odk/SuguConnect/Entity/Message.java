package odk.SuguConnect.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Interface.Utilisateur;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMessage;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Utilisateur sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Utilisateur receiver;
    
    @Column(length = 1000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    private MessageType type;
    
    private LocalDateTime timestamp;
    
    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead = false;
    
    private String filePath;
    
    // Relation avec la conversation
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    
    public enum MessageType {
        TEXT, VOICE, IMAGE, FILE
    }
}