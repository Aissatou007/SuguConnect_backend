package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;

public class ProducteurMapper {
    public static Producteur toEntity(ProducteurRequestDTO producteurRequestDTO , Producteur producteur){
        producteur.setNom(producteurRequestDTO.nom());
        producteur.setPrenom(producteurRequestDTO.prenom());
        producteur.setTelephone(producteurRequestDTO.telephone());
        producteur.setEmail(producteurRequestDTO.email());
        producteur.setLocalisation(producteurRequestDTO.localisation());
        producteur.setLattitude(producteurRequestDTO.latitude());
        producteur.setLongitude(producteurRequestDTO.longitude());
        producteur.setMotDePasse(producteurRequestDTO.motDePasse());
        producteur.setDesription(producteurRequestDTO.description());
        return producteur ;
    }
    public static ProducteurResponseDTO toResponse(Producteur producteur){
        if(producteur == null) return null ;
        ProducteurResponseDTO producteurResponseDTO = new ProducteurResponseDTO(
                producteur.getId(),
                producteur.getNom(),
                producteur.getPrenom(),
                producteur.getTelephone(),
                producteur.getEmail(),
                producteur.getLocalisation(),
                producteur.getLattitude(),
                producteur.getLongitude(),
                producteur.getRole(),
                producteur.getStatutProducteur(),
                producteur.getDesription(),
                producteur.getDateInscription()
        );
        return producteurResponseDTO;
    }
}
