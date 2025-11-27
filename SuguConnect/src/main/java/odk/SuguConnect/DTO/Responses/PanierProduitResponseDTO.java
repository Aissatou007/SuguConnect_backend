package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import odk.SuguConnect.Entity.PanierProduit;
import odk.SuguConnect.Entity.Produit;

@Schema(description = "DTO pour un produit dans le panier avec ses détails")
public record PanierProduitResponseDTO(
        @Schema(description = "ID du PanierProduit")
        int id,
        
        @Schema(description = "Produit")
        ProduitSimpleDTO produit,
        
        @Schema(description = "Quantité dans le panier")
        int quantite,
        
        @Schema(description = "Prix unitaire au moment de l'ajout")
        double prixUnitaire,
        
        @Schema(description = "Prix total pour cette ligne (quantité * prixUnitaire)")
        double prixTotal,
        
        @Schema(description = "Indique si ce produit a déjà été commandé")
        boolean dejaCommande
) {
    public static PanierProduitResponseDTO fromEntity(PanierProduit panierProduit) {
        Produit produit = panierProduit.getProduit();
        ProduitSimpleDTO produitDTO = toProduitSimple(produit);
        
        return new PanierProduitResponseDTO(
                panierProduit.getId(),
                produitDTO,
                panierProduit.getQuantite(),
                panierProduit.getPrixUnitaire(),
                panierProduit.getQuantite() * panierProduit.getPrixUnitaire(),
                panierProduit.isDejaCommande()
        );
    }
    
    private static ProduitSimpleDTO toProduitSimple(Produit produit) {
        if (produit == null) {
            return null;
        }
        
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

