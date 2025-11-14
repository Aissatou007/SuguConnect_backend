package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.ProduitPopulaireDTO;
import odk.SuguConnect.Service.ProduitPopulaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
@Tag(name = "Produits Populaires", description = "API pour récupérer les produits les plus populaires")
public class ProduitPopulaireController {
    
    private final ProduitPopulaireService produitPopulaireService;
    
    @Operation(
        summary = "Récupérer les produits populaires",
        description = "Retourne la liste des produits les plus commandés dans le système, triés par ordre de popularité décroissant"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "Liste des produits populaires récupérée avec succès",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ProduitPopulaireDTO.class)
        )
    )
    @GetMapping("/populaires")
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
    @GetMapping("/populaires/top/{limit}")
    public ResponseEntity<List<ProduitPopulaireDTO>> getTopProduitsPopulaires(
            @Parameter(description = "Nombre maximum de produits à retourner", example = "10")
            @PathVariable int limit) {
        List<ProduitPopulaireDTO> produits = produitPopulaireService.getTopProduitsPopulaires(limit);
        return ResponseEntity.ok(produits);
    }
}