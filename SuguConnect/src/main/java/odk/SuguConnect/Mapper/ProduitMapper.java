package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Responses.ProduitResponseDTO;
import odk.SuguConnect.Entity.Produit;

public class ProduitMapper {
    
    public static ProduitResponseDTO toDto(Produit produit) {
        if (produit == null) {
            return null;
        }
        
        return new ProduitResponseDTO(
                produit.getId(),
                produit.getNom(),
                produit.getDescription(),
                produit.getPrixUnitaire(),
                produit.getUnite(),
                produit.getStockDisponible(),
                produit.isEstBio(),
                produit.getTexteBio(), // "produit 100% bio" ou "produit non bio"
                produit.getPhotos(),
                produit.getProducteur() != null ? produit.getProducteur().getId() : 0,
                produit.getProducteur() != null ? produit.getProducteur().getNom() : "",
                produit.getProducteur() != null ? produit.getProducteur().getPrenom() : "",
                produit.getCategorie() != null ? produit.getCategorie().getId() : 0,
                produit.getCategorie() != null ? produit.getCategorie().getLibelle() : ""
        );
    }
}