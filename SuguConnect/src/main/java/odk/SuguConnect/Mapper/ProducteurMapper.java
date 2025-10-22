package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;

public class ProducteurMapper {
    public static Producteur toEntity(ProducteurRequestDTO producteurRequestDTO, Producteur producteur){
        producteur.setNom(producteurRequestDTO.nom());
        producteur.setPrenom(producteurRequestDTO.prenom());
        producteur.setTelephone(producteurRequestDTO.telephone());
        producteur.setEmail(producteurRequestDTO.email());
        producteur.setLocalisation(producteurRequestDTO.localisation());
        producteur.setLatitude(producteurRequestDTO.latitude());
        producteur.setLongitude(producteurRequestDTO.longitude());
        producteur.setMotDePasse(producteurRequestDTO.motDePasse());
        producteur.setDescription(producteurRequestDTO.description());
        producteur.setNomFerme(producteurRequestDTO.nomFerme());
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
