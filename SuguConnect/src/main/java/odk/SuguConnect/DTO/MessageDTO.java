package odk.SuguConnect.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import odk.SuguConnect.Enums.TypeMessage;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un message dans le système de chat.
 * Contient toutes les informations nécessaires pour afficher un message dans l'interface utilisateur.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    /**
     * Identifiant unique du message
     */
    private Long id;
    
    /**
     * Contenu du message (texte)
     */
    private String contenu;
    
    /**
     * Type du message (TEXTE, IMAGE, VOCAL, DOCUMENT)
     */
    private TypeMessage typeMessage;
    
    /**
     * Date et heure d'envoi du message
     */
    private LocalDateTime dateEnvoi;
    
    /**
     * Chemin du fichier associé au message (pour les images, vocaux et documents)
     */
    private String cheminFichier;
    
    /**
     * Identifiant de l'expéditeur du message
     */
    private int expediteurId;
    
    /**
     * Identifiant du destinataire du message
     */
    private int destinataireId;
    
    /**
     * Identifiant de la conversation à laquelle appartient le message
     */
    private Long conversationId;
    
    /**
     * Indique si le message a été lu par le destinataire
     */
    private boolean lu;
    
    /**
     * Nom complet de l'expéditeur du message
     */
    private String nomExpediteur;
    
    /**
     * Nom complet du destinataire du message
     */
    private String nomDestinataire;
}