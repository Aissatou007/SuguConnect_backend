package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import odk.SuguConnect.DTO.Responses.CategorieResponseDTO;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Mapper.CategorieMapper;
import odk.SuguConnect.Service.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/categorie")
@Tag(name = "Catégorie", description = "API de gestion des catégories de produits")
public class CategorieController {
    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PreAuthorize("hasRole('ADMIN')") // Correction : hasRole avec 'R' majuscule
    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(
            summary = "Créer une catégorie",
            description = "Permet à un administrateur de créer une nouvelle catégorie de produits avec photo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<CategorieResponseDTO> creerCategorie(
            @Parameter(description = "Libellé de la catégorie", required = true)
            @RequestPart String libelle,
            @Parameter(description = "Photo de la catégorie", required = false)
            @RequestPart(required = false) MultipartFile photo) {
        try {
            System.out.println("Requête de création de catégorie reçue");
            System.out.println("Libellé: " + libelle);
            if (photo != null) {
                System.out.println("Photo reçue: " + photo.getOriginalFilename() + " (size: " + photo.getSize() + " bytes)");
            } else {
                System.out.println("Aucune photo reçue");
            }
            
            Categorie categorie = categorieService.creerCategorie(libelle, photo);
            System.out.println("Catégorie créée avec ID: " + categorie.getId());
            if (categorie.getPhotoUrl() != null) {
                System.out.println("Photo URL de la catégorie: " + categorie.getPhotoUrl());
            }
            
            CategorieResponseDTO categorieDto = CategorieMapper.toDto(categorie);
            return ResponseEntity.ok(categorieDto);
        } catch (IOException e) {
            System.err.println("IOException lors de la création de la catégorie: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Exception lors de la création de la catégorie: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{id}") // Ajouter la méthode pour modifier une catégorie
    @Operation(
            summary = "Modifier une catégorie",
            description = "Permet de modifier les informations d'une catégorie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<CategorieResponseDTO> modifierCategorie(
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de la catégorie"
            )
            @RequestBody Categorie categorie) {
        Categorie categorieModifiee = categorieService.modifierCategorie(id, categorie);
        CategorieResponseDTO categorieDto = CategorieMapper.toDto(categorieModifiee);
        return ResponseEntity.ok(categorieDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Supprimer une catégorie",
            description = "Permet de supprimer une catégorie (uniquement si elle ne contient pas de produits)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "Catégorie contient des produits"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<String> supprimerCategorie(
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int id) {
        return ResponseEntity.ok(categorieService.supprimerCategorie(id));
    }

    @GetMapping
    @Operation(
            summary = "Récupérer toutes les catégories",
            description = "Retourne la liste de toutes les catégories disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée")
    public ResponseEntity<List<CategorieResponseDTO>> toutesLesCategories() {
        List<Categorie> categories = categorieService.listerCategorie();
        List<CategorieResponseDTO> categorieDtos = categories.stream()
                .map(CategorieMapper::toDto)
                .toList();
        return ResponseEntity.ok(categorieDtos);
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer une catégorie par son ID",
            description = "Retourne les détails d'une catégorie spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie trouvée"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CategorieResponseDTO> categorieParId(
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int id) {
        Categorie categorie = categorieService.recupererUneCategorie(id);
        CategorieResponseDTO categorieDto = CategorieMapper.toDto(categorie);
        return ResponseEntity.ok(categorieDto);
    }

    @GetMapping(path = "/{id}/produits")
    @Operation(
            summary = "Récupérer les produits d'une catégorie",
            description = "Retourne tous les produits appartenant à une catégorie spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<List<Produit>> produitsParCategorie(
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int id) {
        return ResponseEntity.ok(categorieService.listProduitParCategorie(id));
    }
}