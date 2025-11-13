package odk.SuguConnect.Mapper;

import odk.SuguConnect.DTO.Responses.ProduitSimpleDTO;
import odk.SuguConnect.Entity.Produit;

public class ProduitMapper {
    private static ProduitSimpleDTO toProduitSimple(Produit produit) {
        if (produit == null) return null;

        return new ProduitSimpleDTO(
                produit.getId(),
                produit.getNom(),
                produit.getPrixUnitaire(),
                produit.getProducteur() != null ? produit.getProducteur().getId() : null,
                produit.getProducteur() != null ? produit.getProducteur().getNom() : "Inconnu",
                produit.getProducteur() != null ? produit.getProducteur().getPrenom() : "Inconnu"
        );
    }
}

