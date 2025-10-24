package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO représentant un produit à ajouter à une commande")
public record ProduitCommandeDTO(
        @Schema(description = "ID du produit", example = "1")
        int produitId,
        
        @Schema(description = "Quantité souhaitée", example = "2")
        int quantite
) {
}