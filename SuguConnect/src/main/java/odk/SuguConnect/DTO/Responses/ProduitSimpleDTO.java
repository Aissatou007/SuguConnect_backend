package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO simplifié pour un produit")
public class ProduitSimpleDTO {
    @Schema(description = "ID du produit", example = "1")
    private int id;
    
    @Schema(description = "Nom du produit", example = "Mangues Bio")
    private String nom;
    
    @Schema(description = "Prix unitaire", example = "2500.0")
    private float prixUnitaire;
}