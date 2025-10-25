package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.LivreurRequestDTO;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Service.LivreurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/livreurs")
@RequiredArgsConstructor
@Tag(name = "Livreur", description = "API de gestion des livreurs")
public class LivreurController {
    private final LivreurService livreurService;

    @PostMapping
    @Operation(
            summary = "Créer un livreur",
            description = "Permet à un administrateur de créer un nouveau livreur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou livreur existant")
    })
    public ResponseEntity<LivreurResponseDTO> creerLivreur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du livreur",
                    required = true
            )
            @RequestBody LivreurRequestDTO livreurRequestDTO) {
        LivreurResponseDTO livreur = livreurService.creerLivreur(livreurRequestDTO);
        return ResponseEntity.ok(livreur);
    }

    @GetMapping
    @Operation(
            summary = "Récupérer tous les livreurs",
            description = "Retourne la liste de tous les livreurs"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livreurs récupérée")
    public ResponseEntity<List<LivreurResponseDTO>> recupererTousLesLivreurs() {
        List<LivreurResponseDTO> livreurs = livreurService.recupererTousLesLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer un livreur par son ID",
            description = "Retourne les détails d'un livreur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur trouvé"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    })
    public ResponseEntity<LivreurResponseDTO> recupererUnLivreur(
            @Parameter(description = "ID du livreur", required = true)
            @PathVariable int id) {
        LivreurResponseDTO livreur = livreurService.recupererUnLivreur(id);
        return ResponseEntity.ok(livreur);
    }

    @PutMapping(path = "/{id}")
    @Operation(
            summary = "Modifier un livreur",
            description = "Permet de modifier les informations d'un livreur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Matricule déjà utilisée"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    })
    public ResponseEntity<LivreurResponseDTO> modifierLivreur(
            @Parameter(description = "ID du livreur", required = true)
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du livreur"
            )
            @RequestBody LivreurRequestDTO livreurRequestDTO) {
        LivreurResponseDTO livreur = livreurService.modifierLivreur(livreurRequestDTO, id);
        return ResponseEntity.ok(livreur);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Supprimer un livreur",
            description = "Permet de supprimer un livreur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    })
    public ResponseEntity<String> supprimerLivreur(
            @Parameter(description = "ID du livreur", required = true)
            @PathVariable int id) {
        String message = livreurService.supprimerLivreur(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/disponibles")
    @Operation(
            summary = "Récupérer les livreurs disponibles",
            description = "Retourne la liste des livreurs actuellement disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livreurs disponibles récupérée")
    public ResponseEntity<List<LivreurResponseDTO>> recupererLivreursDisponibles() {
        List<LivreurResponseDTO> livreurs = livreurService.recupererLivreursDisponibles();
        return ResponseEntity.ok(livreurs);
    }

    @PutMapping(path = "/{id}/disponibilite")
    @Operation(
            summary = "Mettre à jour la disponibilité d'un livreur",
            description = "Permet de mettre à jour la disponibilité d'un livreur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    })
    public ResponseEntity<LivreurResponseDTO> mettreAJourDisponibilite(
            @Parameter(description = "ID du livreur", required = true)
            @PathVariable int id,
            @Parameter(description = "Disponibilité du livreur", required = true)
            @RequestParam boolean disponible) {
        LivreurResponseDTO livreur = livreurService.mettreAJourDisponibilite(id, disponible);
        return ResponseEntity.ok(livreur);
    }
}