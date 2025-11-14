package odk.SuguConnect.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO pour représenter une conversation dans le système de chat.
 * Une conversation relie un consommateur et un producteur, éventuellement autour d'un produit spécifique.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDTO {
    /**
     * Identifiant unique de la conversation
     */
    private Long id;
    
    /**
     * Identifiant du consommateur participant à la conversation
     */
    private Integer consommateurId;
    
    /**
     * Identifiant du producteur participant à la conversation
     */
    private Integer producteurId;
    
    /**
     * Identifiant du produit concerné par la conversation (optionnel)
     */
    private Integer produitId;
    
    /**
     * Nom complet du consommateur
     */
    private String nomConsommateur;
    
    /**
     * Nom complet du producteur
     */
    private String nomProducteur;
    
    /**
     * Nom du produit concerné par la conversation (si applicable)
     */
    private String nomProduit;
    
    /**
     * Date et heure de création de la conversation
     */
    private LocalDateTime dateCreation;
    
    /**
     * Date et heure du dernier message envoyé dans la conversation
     */
    private LocalDateTime dateDernierMessage;
    
    /**
     * Indique si la conversation est active
     */
    private boolean active;
}