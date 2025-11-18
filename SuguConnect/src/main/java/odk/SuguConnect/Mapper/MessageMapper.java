package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.MessageDTO;
import odk.SuguConnect.Entity.Message;
import odk.SuguConnect.Enums.TypeMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    
    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }
        
        MessageDTO dto = new MessageDTO();
        dto.setId((long) message.getIdMessage());
        dto.setContenu(message.getContent());
        dto.setTypeMessage(convertMessageTypeToTypeMessage(message.getType()));
        dto.setDateEnvoi(message.getTimestamp());
        dto.setCheminFichier(message.getFilePath());
        dto.setLu(message.isRead());
        
        if (message.getSender() != null) {
            dto.setExpediteurId(message.getSender().getId());
            dto.setNomExpediteur(message.getSender().getNom() + " " + message.getSender().getPrenom());
        }
        
        if (message.getReceiver() != null) {
            dto.setDestinataireId(message.getReceiver().getId());
            dto.setNomDestinataire(message.getReceiver().getNom() + " " + message.getReceiver().getPrenom());
        }
        
        // La nouvelle entité Message n'a pas de conversation, on laisse ce champ vide
        
        return dto;
    }
    
    public Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Message message = new Message();
        message.setIdMessage(dto.getId().intValue());
        message.setContent(dto.getContenu());
        message.setType(convertTypeMessageToMessageType(dto.getTypeMessage()));
        message.setTimestamp(dto.getDateEnvoi());
        message.setFilePath(dto.getCheminFichier());
        message.setRead(dto.isLu());
        
        // Note: Les relations seront définies dans le service/contrôleur
        return message;
    }
    
    private TypeMessage convertMessageTypeToTypeMessage(Message.MessageType messageType) {
        if (messageType == null) {
            return null;
        }
        
        switch (messageType) {
            case TEXT:
                return TypeMessage.TEXTE;
            case IMAGE:
                return TypeMessage.IMAGE;
            case VOICE:
                return TypeMessage.VOCAL;
            case FILE:
                return TypeMessage.DOCUMENT;
            default:
                return TypeMessage.TEXTE;
        }
    }
    
    private Message.MessageType convertTypeMessageToMessageType(TypeMessage typeMessage) {
        if (typeMessage == null) {
            return Message.MessageType.TEXT;
        }
        
        switch (typeMessage) {
            case TEXTE:
                return Message.MessageType.TEXT;
            case IMAGE:
                return Message.MessageType.IMAGE;
            case VOCAL:
                return Message.MessageType.VOICE;
            case DOCUMENT:
                return Message.MessageType.FILE;
            default:
                return Message.MessageType.TEXT;
        }
    }
}