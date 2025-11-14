package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitDetailDTO;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Service.ProduitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Détails des Produits", description = "API pour récupérer les détails complets des produits")
public class ProduitDetailController {
    
    private final ProduitService produitService;
    
    @Operation(
        summary = "Récupérer un produit par son ID",
        description = "Retourne les détails complets d'un produit spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Détails du produit récupérés avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProduitDetailDTO.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @GetMapping("/{produitId}")
    public ResponseEntity<ProduitDetailDTO> getProduitById(
            @Parameter(description = "ID du produit", example = "1")
            @PathVariable int produitId) {
        Produit produit = produitService.getProduitById(produitId);
        ProduitDetailDTO produitDetailDTO = convertToDTO(produit);
        return ResponseEntity.ok(produitDetailDTO);
    }
    
    /**
     * Convertit une entité Produit en DTO ProduitDetailDTO
     * @param produit L'entité produit à convertir
     * @return Le DTO correspondant
     */
    private ProduitDetailDTO convertToDTO(Produit produit) {
        return new ProduitDetailDTO(
            produit.getId(),
            produit.getNom(),
            produit.getDescription(),
            produit.getPrixUnitaire(),
            produit.getUnite(),
            produit.getStockDisponible(),
            produit.getQuantite(),
            produit.getPhotos(),
            produit.getProducteur() != null ? produit.getProducteur().getId() : 0,
            produit.getProducteur() != null ? produit.getProducteur().getNom() : "",
            produit.getProducteur() != null ? produit.getProducteur().getPrenom() : "",
            produit.getProducteur() != null ? produit.getProducteur().getTelephone() : "",
            produit.getProducteur() != null ? produit.getProducteur().getEmail() : "",
            produit.getProducteur() != null ? produit.getProducteur().getLocalisation() : "",
            produit.getCategorie() != null ? produit.getCategorie().getId() : 0,
            produit.getCategorie() != null ? produit.getCategorie().getLibelle() : "",
            0, // noteMoyenne - à implémenter avec le système d'évaluation
            0  // nombreEvaluations - à implémenter avec le système d'évaluation
        );
    }
}