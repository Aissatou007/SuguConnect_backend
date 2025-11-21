package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Responses.StockProduitResponseDTO;
import odk.SuguConnect.Service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/producteur/{producteurId}/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "API de gestion des stocks pour les producteurs")
public class StockController {
    private final StockService stockService;

    @GetMapping(path = "/produits")
    @Operation(
            summary = "Récupérer tous les produits avec leurs informations de stock",
            description = "Retourne la liste de tous les produits du producteur avec leurs informations de stock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<StockProduitResponseDTO>> recupererTousLesProduitsAvecStock(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<StockProduitResponseDTO> produits = stockService.recupererTousLesProduitsAvecStock(producteurId);
        return ResponseEntity.ok(produits);
    }

    @PutMapping(path = "/produits/{produitId}/quantite")
    @Operation(
            summary = "Mettre à jour la quantité d'un produit",
            description = "Permet de mettre à jour la quantité en stock d'un produit"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantité mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Quantité invalide"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<StockProduitResponseDTO> mettreAJourQuantite(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int produitId,
            @Parameter(description = "Nouvelle quantité", required = true)
            @RequestParam int nouvelleQuantite) {
        
        StockProduitResponseDTO produit = stockService.mettreAJourQuantite(produitId, producteurId, nouvelleQuantite);
        return ResponseEntity.ok(produit);
    }

    @PutMapping(path = "/produits/{produitId}/alerte")
    @Operation(
            summary = "Mettre à jour le seuil d'alerte de stock",
            description = "Permet de mettre à jour le seuil d'alerte de stock d'un produit"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seuil d'alerte mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Seuil d'alerte invalide"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<StockProduitResponseDTO> mettreAJourSeuilAlerte(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int produitId,
            @Parameter(description = "Nouveau seuil d'alerte", required = true)
            @RequestParam int seuilAlerte) {
        
        StockProduitResponseDTO produit = stockService.mettreAJourSeuilAlerte(produitId, producteurId, seuilAlerte);
        return ResponseEntity.ok(produit);
    }
}