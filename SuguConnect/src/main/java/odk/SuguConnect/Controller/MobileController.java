package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitDTO;
import odk.SuguConnect.DTO.ProduitPopulaireDTO;
import odk.SuguConnect.Service.ProduitCategorieService;
import odk.SuguConnect.Service.ProduitPopulaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
@Tag(name = "API Mobile", description = "API dédiée aux applications mobiles")
public class MobileController {
    
    private final ProduitCategorieService produitCategorieService;
    private final ProduitPopulaireService produitPopulaireService;
    
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
    @GetMapping("/categorie/{categorieId}/produits")
    public ResponseEntity<List<ProduitDTO>> getProduitsParCategorie(
            @Parameter(description = "ID de la catégorie", example = "1")
            @PathVariable int categorieId) {
        List<ProduitDTO> produits = produitCategorieService.getProduitsDisponiblesParCategorie(categorieId);
        return ResponseEntity.ok(produits);
    }
    
    @Operation(
        summary = "Récupérer les produits populaires",
        description = "Retourne la liste des produits les plus commandés dans le système"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Liste des produits populaires récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProduitPopulaireDTO.class)
        )
    )
    @GetMapping("/produits/populaires")
    public ResponseEntity<List<ProduitPopulaireDTO>> getProduitsPopulaires() {
        List<ProduitPopulaireDTO> produits = produitPopulaireService.getProduitsPopulaires();
        return ResponseEntity.ok(produits);
    }
    
    @Operation(
        summary = "Récupérer les N produits les plus populaires",
        description = "Retourne une liste limitée des produits les plus commandés dans le système"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Liste limitée des produits populaires récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProduitPopulaireDTO.class)
        )
    )
    @GetMapping("/produits/populaires/top/{limit}")
    public ResponseEntity<List<ProduitPopulaireDTO>> getTopProduitsPopulaires(
            @Parameter(description = "Nombre maximum de produits à retourner", example = "10")
            @PathVariable int limit) {
        List<ProduitPopulaireDTO> produits = produitPopulaireService.getTopProduitsPopulaires(limit);
        return ResponseEntity.ok(produits);
    }
}