package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de réponse pour un produit dans une commande")
public record CommandeProduitResponseDTO(
        @Schema(description = "ID de l'article de commande", example = "1")
        int id,
        
        @Schema(description = "Informations du produit")
        ProduitSimpleDTO produit,
        
        @Schema(description = "Quantité commandée", example = "2")
        int quantite,
        
        @Schema(description = "Prix unitaire au moment de la commande", example = "2500.0")
        float prixUnitaire
) {
}