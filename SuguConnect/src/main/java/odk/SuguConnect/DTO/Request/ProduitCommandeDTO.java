package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO représentant un produit à ajouter à une commande")
public class ProduitCommandeDTO {
    @Schema(description = "ID du produit", example = "1")
    private int produitId;
    
    @Schema(description = "Quantité souhaitée", example = "2")
    private int quantite;
}