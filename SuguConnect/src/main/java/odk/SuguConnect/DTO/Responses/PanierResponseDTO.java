package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Entity.Panier;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "DTO pour le panier d'un consommateur")
public record PanierResponseDTO(
        @Schema(description = "ID du panier")
        int id,
        
        @Schema(description = "Liste des produits dans le panier avec leurs détails")
        List<PanierProduitResponseDTO> produits,
        
        @Schema(description = "Nombre total de produits dans le panier")
        int nombreProduits,
        
        @Schema(description = "Prix total du panier")
        double prixTotal
) {
    public static PanierResponseDTO fromEntity(Panier panier) {
        List<PanierProduitResponseDTO> produitsDTO = panier.getPanierProduits() != null
                ? panier.getPanierProduits().stream()
                        .map(PanierProduitResponseDTO::fromEntity)
                        .collect(Collectors.toList())
                : List.of();
        
        double prixTotal = produitsDTO.stream()
                .mapToDouble(PanierProduitResponseDTO::prixTotal)
                .sum();
        
        return new PanierResponseDTO(
                panier.getId(),
                produitsDTO,
                produitsDTO.size(),
                prixTotal
        );
    }
}


