package odk.SuguConnect.DTO.Mapper;

import odk.SuguConnect.DTO.Responses.StockProduitResponseDTO;
import odk.SuguConnect.Entity.Produit;

public class StockProduitMapper {
    
    public static StockProduitResponseDTO toStockProduitResponse(Produit produit) {
        String statut = determinerStatut(produit);
        double valeurTotale = produit.getQuantite() * produit.getPrixUnitaire();
        
        return StockProduitResponseDTO.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .prix((double) produit.getPrixUnitaire())
                .quantite(produit.getQuantite())
                .seuilAlerte(produit.getSeuilAlerte())
                .unite(produit.getUnite() != null ? produit.getUnite().name() : "")
                .categorie(produit.getCategorie() != null ? produit.getCategorie().getLibelle() : "")
                .bio(false) // Valeur par défaut, à ajuster selon la logique métier
                .imageUrl(produit.getPhotos() != null && !produit.getPhotos().isEmpty() ? 
                        "/files/download/" + produit.getPhotos().get(0) : "")
                .dateCreation(produit.getProducteur() != null ? 
                        produit.getProducteur().getDateInscription() : null)
                .dateMiseAJour(null) // À ajuster selon les champs disponibles
                .statut(statut)
                .valeurTotale(valeurTotale)
                .build();
    }
    
    private static String determinerStatut(Produit produit) {
        if (produit.getQuantite() <= 0) {
            return "epuise";
        } else if (produit.getQuantite() <= produit.getSeuilAlerte()) {
            return "stock_faible";
        } else {
            return "en_stock";
        }
    }
}