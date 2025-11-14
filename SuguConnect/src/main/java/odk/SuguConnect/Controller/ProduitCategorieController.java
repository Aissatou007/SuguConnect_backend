package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitDTO;
import odk.SuguConnect.Service.ProduitCategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits par Catégorie", description = "API pour récupérer les produits par catégorie")
public class ProduitCategorieController {
    
    private final ProduitCategorieService produitCategorieService;
    
    @Operation(
        summary = "Récupérer les produits d'une catégorie",
        description = "Retourne la liste des produits appartenant à une catégorie spécifique"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Liste des produits de la catégorie récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProduitDTO.class)
        )
    )
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ProduitDTO>> getProduitsParCategorie(
            @Parameter(description = "ID de la catégorie", example = "1")
            @PathVariable int categorieId) {
        List<ProduitDTO> produits = produitCategorieService.getProduitsParCategorie(categorieId);
        return ResponseEntity.ok(produits);
    }
    
    @Operation(
        summary = "Récupérer les produits disponibles d'une catégorie",
        description = "Retourne la liste des produits disponibles (en stock) appartenant à une catégorie spécifique"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Liste des produits disponibles de la catégorie récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProduitDTO.class)
        )
    )
    @GetMapping("/categorie/{categorieId}/disponibles")
    public ResponseEntity<List<ProduitDTO>> getProduitsDisponiblesParCategorie(
            @Parameter(description = "ID de la catégorie", example = "1")
            @PathVariable int categorieId) {
        List<ProduitDTO> produits = produitCategorieService.getProduitsDisponiblesParCategorie(categorieId);
        return ResponseEntity.ok(produits);
    }
}