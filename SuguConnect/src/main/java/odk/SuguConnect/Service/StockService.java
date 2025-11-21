package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Mapper.StockProduitMapper;
import odk.SuguConnect.DTO.Responses.StockProduitResponseDTO;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Repository.ProduitRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {
    private final ProduitRepository produitRepository;
    private final ProducteurRepository producteurRepository;

    /**
     * Récupère tous les produits avec leurs informations de stock pour un producteur
     * @param producteurId ID du producteur
     * @return Liste des produits du producteur
     * @throws EntityNotFoundException si le producteur n'existe pas
     */
    public List<StockProduitResponseDTO> recupererTousLesProduitsAvecStock(int producteurId) {
        Producteur producteur = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé avec l'ID: " + producteurId));
        
        List<Produit> produits = produitRepository.findByProducteur(producteur);
        return produits.stream()
                .map(StockProduitMapper::toStockProduitResponse)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour la quantité en stock d'un produit
     * @param produitId ID du produit
     * @param producteurId ID du producteur (pour vérification)
     * @param nouvelleQuantite Nouvelle quantité
     * @return Le produit mis à jour
     * @throws EntityNotFoundException si le produit n'existe pas
     * @throws SecurityException si le producteur n'est pas autorisé
     * @throws IllegalArgumentException si la quantité est négative
     */
    public StockProduitResponseDTO mettreAJourQuantite(int produitId, int producteurId, int nouvelleQuantite) {
        if (nouvelleQuantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + produitId));

        if (produit.getProducteur().getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        produit.setQuantite(nouvelleQuantite);
        produit.setStockDisponible(nouvelleQuantite);
        
        Produit produitMisAJour = produitRepository.save(produit);
        return StockProduitMapper.toStockProduitResponse(produitMisAJour);
    }

    /**
     * Met à jour le seuil d'alerte de stock d'un produit
     * @param produitId ID du produit
     * @param producteurId ID du producteur (pour vérification)
     * @param seuilAlerte Nouveau seuil d'alerte
     * @return Le produit mis à jour
     * @throws EntityNotFoundException si le produit n'existe pas
     * @throws SecurityException si le producteur n'est pas autorisé
     * @throws IllegalArgumentException si le seuil d'alerte est négatif
     */
    public StockProduitResponseDTO mettreAJourSeuilAlerte(int produitId, int producteurId, int seuilAlerte) {
        if (seuilAlerte < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas être négatif");
        }

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + produitId));

        if (produit.getProducteur().getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        produit.setSeuilAlerte(seuilAlerte);
        
        Produit produitMisAJour = produitRepository.save(produit);
        return StockProduitMapper.toStockProduitResponse(produitMisAJour);
    }

    /**
     * Ajoute de la quantité au stock existant
     * @param produitId ID du produit
     * @param producteurId ID du producteur (pour vérification)
     * @param quantiteAAjouter Quantité à ajouter
     * @return Le produit mis à jour
     * @throws EntityNotFoundException si le produit n'existe pas
     * @throws SecurityException si le producteur n'est pas autorisé
     * @throws IllegalArgumentException si la quantité à ajouter est négative
     */
    public StockProduitResponseDTO ajouterAuStock(int produitId, int producteurId, int quantiteAAjouter) {
        if (quantiteAAjouter < 0) {
            throw new IllegalArgumentException("La quantité à ajouter ne peut pas être négative");
        }

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + produitId));

        if (produit.getProducteur().getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        int nouvelleQuantite = produit.getQuantite() + quantiteAAjouter;
        produit.setQuantite(nouvelleQuantite);
        produit.setStockDisponible(nouvelleQuantite);
        
        Produit produitMisAJour = produitRepository.save(produit);
        return StockProduitMapper.toStockProduitResponse(produitMisAJour);
    }

    /**
     * Retire de la quantité du stock existant
     * @param produitId ID du produit
     * @param producteurId ID du producteur (pour vérification)
     * @param quantiteARetirer Quantité à retirer
     * @return Le produit mis à jour
     * @throws EntityNotFoundException si le produit n'existe pas
     * @throws SecurityException si le producteur n'est pas autorisé
     * @throws IllegalArgumentException si la quantité à retirer est négative ou supérieure au stock disponible
     */
    public StockProduitResponseDTO retirerDuStock(int produitId, int producteurId, int quantiteARetirer) {
        if (quantiteARetirer < 0) {
            throw new IllegalArgumentException("La quantité à retirer ne peut pas être négative");
        }

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + produitId));

        if (produit.getProducteur().getId() != producteurId) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        if (quantiteARetirer > produit.getQuantite()) {
            throw new IllegalArgumentException("La quantité à retirer ne peut pas être supérieure au stock disponible");
        }

        int nouvelleQuantite = produit.getQuantite() - quantiteARetirer;
        produit.setQuantite(nouvelleQuantite);
        produit.setStockDisponible(nouvelleQuantite);
        
        Produit produitMisAJour = produitRepository.save(produit);
        return StockProduitMapper.toStockProduitResponse(produitMisAJour);
    }
}