package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO contenant une catégorie et la liste des produits existants dans cette catégorie")
public record ProduitsParCategorieDTO(
        @Schema(description = "ID de la catégorie")
        int categorieId,
        
        @Schema(description = "Nom de la catégorie")
        String categorieNom,
        
        @Schema(description = "Liste des produits existants dans cette catégorie")
        List<ProduitSimpleDTO> produitsExistants
) {
}