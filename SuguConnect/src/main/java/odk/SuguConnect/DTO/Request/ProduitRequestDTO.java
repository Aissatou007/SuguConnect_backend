package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.Unite;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO pour l'ajout/modification d'un produit avec photos")
public class ProduitRequestDTO {
    @Schema(description = "Nom du produit", example = "Mangues Bio")
    private String nom;
    
    @Schema(description = "Description du produit", example = "Mangues fraîches biologiques de notre ferme")
    private String description;
    
    @Schema(description = "Prix unitaire", example = "2500.0")
    private float prixUnitaire;
    
    @Schema(
        description = "Unité de mesure du produit",
        example = "KILOGRAMME",
        allowableValues = {"KILOGRAMME", "GRAMME", "TONNE", "LITRE", "MILLILITRE", "SAC", "BOTTE", "PIECE"}
    )
    private Unite unite;
    
    @Schema(description = "Stock disponible", example = "100")
    private int stockDisponible;
    
    @Schema(description = "ID de la catégorie", example = "1")
    private int categorieId;
    
    @Schema(description = "URLs des photos du produit")
    private List<String> photos;
}
