package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Enums.Unite;

import java.util.List;

@Schema(description = "DTO pour les détails complets d'un produit")
public record ProduitResponseDTO(
        int id,
        String nom,
        String description,
        float prixUnitaire,
        Unite unite,
        int stockDisponible,
        boolean estBio,
        String texteBio, // "produit 100% bio" ou "produit non bio"
        List<String> photos,
        int producteurId,
        String producteurNom,
        String producteurPrenom,
        int categorieId,
        String categorieNom
) {
}