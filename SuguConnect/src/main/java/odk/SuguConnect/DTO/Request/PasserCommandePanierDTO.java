package odk.SuguConnect.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.ModePaiement;

import java.util.List;

@Schema(description = "DTO pour passer une commande avec des produits spécifiques du panier")
public record PasserCommandePanierDTO(
        @Schema(
                description = "Liste des produits du panier à commander avec leurs quantités",
                example = "[{\"produitId\": 1, \"quantite\": 2}, {\"produitId\": 3, \"quantite\": 1}]"
        )
        List<ProduitCommandeDTO> produits,
        
        @Schema(
                description = "Mode de paiement choisi",
                example = "ORANGE_MONEY",
                allowableValues = {"ORANGE_MONEY", "WAVE", "ESPECES", "MOBILE_MONEY"}
        )
        ModePaiement modePaiement,
        
        @Schema(
                description = "Numéro de téléphone pour paiement mobile (requis si modePaiement = ORANGE_MONEY, WAVE ou MOBILE_MONEY)",
                example = "70123456"
        )
        String numeroTelephone
) {
}
