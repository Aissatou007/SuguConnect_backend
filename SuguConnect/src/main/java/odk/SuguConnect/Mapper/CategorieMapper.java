package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Responses.CategorieResponseDTO;
import odk.SuguConnect.Entity.Categorie;

public class CategorieMapper {
    
    public static CategorieResponseDTO toDto(Categorie categorie) {
        if (categorie == null) {
            return null;
        }
        
        return new CategorieResponseDTO(
                categorie.getId(),
                categorie.getLibelle(),
                categorie.getDateAjout(),
                categorie.getPhotoUrl()
        );
    }
}