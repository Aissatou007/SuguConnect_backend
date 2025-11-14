package odk.SuguConnect.Service;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitDTO;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitCategorieService {
    
    private final ProduitRepository produitRepository;
    
    /**
     * Récupère les produits par catégorie
     * @param categorieId ID de la catégorie
     * @return Liste des produits de la catégorie
     */
    public List<ProduitDTO> getProduitsParCategorie(int categorieId) {
        List<Produit> produits = produitRepository.findByCategorieId(categorieId);
        return convertToDTOs(produits);
    }
    
    /**
     * Récupère les produits par catégorie avec stock disponible
     * @param categorieId ID de la catégorie
     * @return Liste des produits de la catégorie avec stock disponible
     */
    public List<ProduitDTO> getProduitsDisponiblesParCategorie(int categorieId) {
        List<Produit> produits = produitRepository.findByCategorieId(categorieId);
        // Filtrer les produits avec stock disponible
        List<Produit> produitsDisponibles = produits.stream()
                .filter(produit -> produit.getStockDisponible() > 0)
                .collect(Collectors.toList());
        return convertToDTOs(produitsDisponibles);
    }
    
    /**
     * Convertit une liste d'entités Produit en liste de DTOs
     * @param produits Liste des entités Produit
     * @return Liste de DTOs
     */
    private List<ProduitDTO> convertToDTOs(List<Produit> produits) {
        return produits.stream().map(produit -> {
            ProduitDTO dto = new ProduitDTO();
            dto.setProduitId(produit.getId());
            dto.setNomProduit(produit.getNom());
            dto.setDescription(produit.getDescription());
            dto.setPrixUnitaire(produit.getPrixUnitaire());
            dto.setUnite(produit.getUnite());
            dto.setStockDisponible(produit.getStockDisponible());
            dto.setProducteurId(produit.getProducteur() != null ? produit.getProducteur().getId() : 0);
            dto.setNomProducteur(produit.getProducteur() != null ? 
                produit.getProducteur().getNom() + " " + produit.getProducteur().getPrenom() : "");
            dto.setCategorieId(produit.getCategorie() != null ? produit.getCategorie().getId() : 0);
            dto.setLibelleCategorie(produit.getCategorie() != null ? produit.getCategorie().getLibelle() : "");
            
            // Définir la première photo si elle existe
            if (produit.getPhotos() != null && !produit.getPhotos().isEmpty()) {
                dto.setPhotoUrl(produit.getPhotos().get(0));
            } else {
                dto.setPhotoUrl(""); // Aucune photo
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}