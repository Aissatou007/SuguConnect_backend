package odk.SuguConnect.Service;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitPopulaireDTO;
import odk.SuguConnect.Repository.ProduitRepository;
import odk.SuguConnect.Entity.Produit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitPopulaireService {
    
    private final ProduitRepository produitRepository;
    
    /**
     * Récupère les produits les plus populaires (les plus commandés)
     * @return Liste des produits populaires
     */
    public List<ProduitPopulaireDTO> getProduitsPopulaires() {
        List<ProduitPopulaireDTO> produits = produitRepository.findTopProduitsPopulaires();
        
        // Si aucun produit n'est commandé, retourner tous les produits
        if (produits.isEmpty()) {
            produits = getAllProduitsAsPopular();
        }
        
        // Retourner les 10 premiers produits par défaut
        List<ProduitPopulaireDTO> result = produits.size() > 10 ? produits.subList(0, 10) : produits;
        return enrichirAvecPhoto(result);
    }
    
    /**
     * Récupère les N produits les plus populaires
     * @param limit Nombre maximum de produits à retourner
     * @return Liste des produits populaires limitée
     */
    public List<ProduitPopulaireDTO> getTopProduitsPopulaires(int limit) {
        List<ProduitPopulaireDTO> produits = produitRepository.findTopProduitsPopulaires();
        
        // Si aucun produit n'est commandé, retourner tous les produits
        if (produits.isEmpty()) {
            produits = getAllProduitsAsPopular();
        }
        
        List<ProduitPopulaireDTO> result = produits.size() > limit ? produits.subList(0, limit) : produits;
        return enrichirAvecPhoto(result);
    }
    
    /**
     * Récupère tous les produits et les transforme en DTOs populaires
     * Utilisé quand il n'y a pas encore de commandes
     * @return Liste de tous les produits comme s'ils étaient populaires
     */
    private List<ProduitPopulaireDTO> getAllProduitsAsPopular() {
        List<Produit> produits = produitRepository.findAll();
        return produits.stream().map(produit -> {
            ProduitPopulaireDTO dto = new ProduitPopulaireDTO();
            dto.setProduitId(produit.getId());
            dto.setNomProduit(produit.getNom());
            dto.setDescription(produit.getDescription());
            dto.setPrixUnitaire(produit.getPrixUnitaire());
            dto.setUnite(produit.getUnite() != null ? produit.getUnite().name() : "");
            dto.setNombreCommandes(0); // Aucune commande pour le moment
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
    
    /**
     * Enrichit les DTOs avec la première photo du produit
     * @param dtos Liste des DTOs à enrichir
     * @return Liste enrichie avec les photos
     */
    private List<ProduitPopulaireDTO> enrichirAvecPhoto(List<ProduitPopulaireDTO> dtos) {
        return dtos.stream().map(dto -> {
            // Récupérer le produit complet pour obtenir ses photos
            Produit produit = produitRepository.findById(dto.getProduitId()).orElse(null);
            if (produit != null && produit.getPhotos() != null && !produit.getPhotos().isEmpty()) {
                dto.setPhotoUrl(produit.getPhotos().get(0));
            }
            return dto;
        }).collect(Collectors.toList());
    }
}