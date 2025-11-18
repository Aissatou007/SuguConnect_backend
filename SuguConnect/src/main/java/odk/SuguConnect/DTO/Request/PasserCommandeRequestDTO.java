package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import odk.SuguConnect.Enums.ModePaiement;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO pour passer une commande avec des produits spécifiques")
public class PasserCommandeRequestDTO {
    @Schema(
            description = "Liste des produits à commander",
            example = "[{\"produitId\": 1, \"quantite\": 2}, {\"produitId\": 3, \"quantite\": 1}]"
    )
    private List<ProduitCommandeDTO> produits;
    
    @Schema(
            description = "Mode de paiement choisi",
            example = "ORANGE_MONEY",
            allowableValues = {"ORANGE_MONEY", "WAVE", "ESPECES"}
    )
    private ModePaiement modePaiement;
}