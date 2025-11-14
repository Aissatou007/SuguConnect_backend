package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.MessageDTO;
import odk.SuguConnect.Entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    
    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContenu(message.getContenu());
        dto.setTypeMessage(message.getTypeMessage());
        dto.setDateEnvoi(message.getDateEnvoi());
        dto.setCheminFichier(message.getCheminFichier());
        dto.setLu(message.isLu());
        
        if (message.getExpediteur() != null) {
            dto.setExpediteurId(message.getExpediteur().getId());
            dto.setNomExpediteur(message.getExpediteur().getNom() + " " + message.getExpediteur().getPrenom());
        }
        
        if (message.getDestinataire() != null) {
            dto.setDestinataireId(message.getDestinataire().getId());
            dto.setNomDestinataire(message.getDestinataire().getNom() + " " + message.getDestinataire().getPrenom());
        }
        
        if (message.getConversation() != null) {
            dto.setConversationId(message.getConversation().getId());
        }
        
        return dto;
    }
    
    public Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Message message = new Message();
        message.setId(dto.getId());
        message.setContenu(dto.getContenu());
        message.setTypeMessage(dto.getTypeMessage());
        message.setDateEnvoi(dto.getDateEnvoi());
        message.setCheminFichier(dto.getCheminFichier());
        message.setLu(dto.isLu());
        
        // Note: Les relations seront définies dans le service/contrôleur
        return message;
    }
}