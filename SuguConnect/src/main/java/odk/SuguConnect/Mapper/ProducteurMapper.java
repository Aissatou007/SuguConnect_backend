package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;

public class ProducteurMapper {
    public static Producteur toEntity(ProducteurRequestDTO producteurRequestDTO, Producteur producteur){
        producteur.setNom(producteurRequestDTO.getNom());
        producteur.setPrenom(producteurRequestDTO.getPrenom());
        producteur.setTelephone(producteurRequestDTO.getTelephone());
        producteur.setEmail(producteurRequestDTO.getEmail());
        producteur.setLocalisation(producteurRequestDTO.getLocalisation());
        producteur.setLatitude(producteurRequestDTO.getLatitude());
        producteur.setLongitude(producteurRequestDTO.getLongitude());
        producteur.setMotDePasse(producteurRequestDTO.getMotDePasse());
        producteur.setDescription(producteurRequestDTO.getDescription());
        producteur.setNomFerme(producteurRequestDTO.getNomFerme());
        return producteur;
    }
    public static ProducteurResponseDTO toResponse(Producteur producteur){
        if(producteur == null) return null;
        ProducteurResponseDTO producteurResponseDTO = new ProducteurResponseDTO(
                producteur.getId(),
                producteur.getNom(),
                producteur.getPrenom(),
                producteur.getTelephone(),
                producteur.getEmail(),
                producteur.getLocalisation(),
                producteur.getLatitude(),
                producteur.getLongitude(),
                producteur.getRole(),
                producteur.getStatutProducteur(),
                producteur.getDescription(),
                producteur.getNomFerme(),
                producteur.getPhotoUrl(),
                producteur.getDateInscription(),
                producteur.getMotifDeRejet()
        );
        return producteurResponseDTO;
    }
}
