package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Unite;

import java.util.List;

@Schema(description = "DTO pour l'ajout/modification d'un produit avec photos")
public record ProduitRequestDTO(
        @Schema(description = "Nom du produit", example = "Mangues Bio")
        String nom,
        
        @Schema(description = "Description du produit", example = "Mangues fraîches biologiques de notre ferme")
        String description,
        
        @Schema(description = "Prix unitaire", example = "2500.0")
        float prixUnitaire,
        
        @Schema(
            description = "Unité de mesure du produit",
            example = "KILOGRAMME",
            allowableValues = {"KILOGRAMME", "GRAMME", "TONNE", "LITRE", "MILLILITRE", "SAC", "BOTTE", "PIECE"}
        )
        Unite unite,
        
        @Schema(description = "Stock disponible", example = "100")
        int stockDisponible,
        
        @Schema(description = "ID de la catégorie", example = "1")
        int categorieId,
        
        @Schema(description = "Indique si le produit est biologique", example = "true")
        Boolean estBio,
        
        @Schema(description = "URLs des photos du produit")
        List<String> photos
) {
}
