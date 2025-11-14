package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.ConversationDTO;
import odk.SuguConnect.Entity.Conversation;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {
    
    public ConversationDTO toDTO(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setDateCreation(conversation.getDateCreation());
        dto.setDateDernierMessage(conversation.getDateDernierMessage());
        dto.setActive(conversation.isActive());
        
        if (conversation.getConsommateur() != null) {
            dto.setConsommateurId(conversation.getConsommateur().getId());
            dto.setNomConsommateur(conversation.getConsommateur().getNom() + " " + 
                                 conversation.getConsommateur().getPrenom());
        }
        
        if (conversation.getProducteur() != null) {
            dto.setProducteurId(conversation.getProducteur().getId());
            dto.setNomProducteur(conversation.getProducteur().getNom() + " " + 
                               conversation.getProducteur().getPrenom());
        }
        
        if (conversation.getProduit() != null) {
            dto.setProduitId(conversation.getProduit().getId());
            dto.setNomProduit(conversation.getProduit().getNom());
        }
        
        return dto;
    }
    
    public Conversation toEntity(ConversationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Conversation conversation = new Conversation();
        conversation.setId(dto.getId());
        conversation.setDateCreation(dto.getDateCreation());
        conversation.setDateDernierMessage(dto.getDateDernierMessage());
        conversation.setActive(dto.isActive());
        
        // Note: Les relations seront définies dans le service/contrôleur
        return conversation;
    }
}