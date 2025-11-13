package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Responses.AvisResponseDTO;
import odk.SuguConnect.Entity.Avis;
import odk.SuguConnect.Service.AvisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avis")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "API de gestion des avis et évaluations")
public class AvisController {

    private final AvisService avisService;

    @PostMapping("/commande/{commandeId}")
    @PreAuthorize("hasAnyRole('CONSOMMATEUR', 'ADMIN')")
    @Operation(
            summary = "Donner un avis sur une commande",
            description = "Permet au consommateur de donner une note et un commentaire après avoir validé la réception de sa commande"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avis créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou commande non éligible"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<Avis> creerAvis(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du consommateur", required = true)
            @RequestParam int consommateurId,
            @Parameter(description = "Note de 1 à 5", required = true)
            @RequestParam int note,
            @Parameter(description = "Commentaire", required = false)
            @RequestParam(required = false) String commentaire) {
        
        Avis avis = avisService.creerAvis(commandeId, consommateurId, note, commentaire);
        return ResponseEntity.status(HttpStatus.CREATED).body(avis);
    }

    @GetMapping("/producteur/{producteurId}")
    @Operation(
            summary = "Voir les avis d'un producteur",
            description = "Récupère tous les avis validés d'un producteur"
    )
    @ApiResponse(responseCode = "200", description = "Liste des avis récupérée")
    public ResponseEntity<List<Avis>> getAvisProducteur(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        
        List<Avis> avis = avisService.getAvisProducteur(producteurId);
        return ResponseEntity.ok(avis);
    }

    @GetMapping("/producteur/{producteurId}/moyenne")
    @Operation(
            summary = "Obtenir la moyenne des notes d'un producteur",
            description = "Calcule et retourne la moyenne des notes d'un producteur"
    )
    @ApiResponse(responseCode = "200", description = "Moyenne calculée")
    public ResponseEntity<Double> getMoyenneNotes(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        
        double moyenne = avisService.getMoyenneNotesProducteur(producteurId);
        return ResponseEntity.ok(moyenne);
    }

    @GetMapping("/commande/{commandeId}")
    @Operation(
            summary = "Voir l'avis d'une commande",
            description = "Récupère l'avis donné pour une commande spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avis trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun avis pour cette commande")
    })
    public ResponseEntity<Avis> getAvisCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId) {
        
        Avis avis = avisService.getAvisCommande(commandeId);
        if (avis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avis);
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les avis", description = "Retourne la liste complète des avis pour les administrateurs")
    @ApiResponse(responseCode = "200", description = "Liste des avis récupérée avec succès")
    public ResponseEntity<List<AvisResponseDTO>> getAllAvis() {
        List<AvisResponseDTO> avisList = avisService.getAllAvisDTO();
        return ResponseEntity.ok(avisList);
    }
}
