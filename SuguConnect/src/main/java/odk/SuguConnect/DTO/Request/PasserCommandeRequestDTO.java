package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.ModePaiement;

import java.util.List;

@Schema(description = "DTO pour passer une commande avec des produits spécifiques")
public record PasserCommandeRequestDTO(
        @Schema(
                description = "Liste des produits à commander",
                example = "[{\"produitId\": 1, \"quantite\": 2}, {\"produitId\": 3, \"quantite\": 1}]"
        )
        List<ProduitCommandeDTO> produits,
        
        @Schema(
                description = "Mode de paiement choisi",
                example = "ORANGE_MONEY",
                allowableValues = {"ORANGE_MONEY", "WAVE", "ESPECES"}
        )
        ModePaiement modePaiement
) {
}