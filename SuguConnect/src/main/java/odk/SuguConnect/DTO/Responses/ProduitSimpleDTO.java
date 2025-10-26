package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simplifié pour un produit")
public record ProduitSimpleDTO(
        @Schema(description = "ID du produit", example = "1")
        int id,
        
        @Schema(description = "Nom du produit", example = "Mangues Bio")
        String nom,
        
        @Schema(description = "Prix unitaire", example = "2500.0")
        float prixUnitaire,
        
        @Schema(description = "ID du producteur qui a créé le produit", example = "1")
        Integer producteurId
) {
}