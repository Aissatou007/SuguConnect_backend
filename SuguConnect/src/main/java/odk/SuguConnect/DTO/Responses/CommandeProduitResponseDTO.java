package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO de réponse pour un produit dans une commande")
public class CommandeProduitResponseDTO {
    @Schema(description = "ID de l'article de commande", example = "1")
    private int id;
    
    @Schema(description = "Informations du produit")
    private ProduitSimpleDTO produit;
    
    @Schema(description = "Quantité commandée", example = "2")
    private int quantite;
    
    @Schema(description = "Prix unitaire au moment de la commande", example = "2500.0")
    private float prixUnitaire;
}