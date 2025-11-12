package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO simplifié pour un produit")
public record ProduitSimpleDTO(
        int id,
        String nom,
        float prixUnitaire,
        Integer producteurId,
        String producteurNom,
        String producteurPrenom
) {}