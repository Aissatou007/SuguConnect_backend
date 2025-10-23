package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@Tag(name = "Administrateur", description = "API de gestion des administrateurs")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(path = "/inscription")
    @Operation(
            summary = "Créer un nouvel administrateur",
            description = "Permet de créer un nouveau compte administrateur. Nécessite les privilèges administrateur."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = AdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Privilèges administrateur requis")
    })
    public ResponseEntity<AdminResponseDTO> inscription(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du nouvel administrateur",
                    required = true
            )
            @RequestBody AdminRequestDTO adminRequestDTO){
        AdminResponseDTO admin = adminService.createAdmin(adminRequestDTO);
        return ResponseEntity.ok(admin);
    }

    @GetMapping(path = "/admins")
    @Operation(
            summary = "Récupérer tous les administrateurs",
            description = "Retourne la liste complète de tous les administrateurs"
    )
    @ApiResponse(responseCode = "200", description = "Liste des administrateurs récupérée avec succès")
    public ResponseEntity<List<AdminResponseDTO>> recupererTousLesAdmins(){
        List<AdminResponseDTO> admins = adminService.recupererLesAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer un administrateur par son ID",
            description = "Retourne les détails d'un administrateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    })
    public ResponseEntity<AdminResponseDTO> recupererUnAdmin(
            @Parameter(description = "ID de l'administrateur", required = true)
            @PathVariable int id){
        AdminResponseDTO admin =  adminService.recupererUnAdmin(id);
        return ResponseEntity.ok(admin);
    }

    @PutMapping(path = "/{id}")
    @Operation(
            summary = "Modifier un administrateur",
            description = "Permet de modifier les informations d'un administrateur existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrateur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    })
    public ResponseEntity<String> modifier(
            @Parameter(description = "ID de l'administrateur", required = true)
            @PathVariable int id ,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de l'administrateur"
            )
            @RequestBody AdminRequestDTO adminRequestDTO){
        String message = adminService.modifierInformationAdmin(adminRequestDTO , id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Supprimer un administrateur",
            description = "Permet de supprimer un compte administrateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    })
    public ResponseEntity<String> supprimer(
            @Parameter(description = "ID de l'administrateur", required = true)
            @PathVariable int id){
        String message = adminService.supprimerAdmin(id);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping(path = "/{id}/toggle-status")
    @Operation(
            summary = "Activer/Désactiver un administrateur",
            description = "Permet d'activer ou désactiver le compte d'un administrateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    })
    public ResponseEntity<String> toggleAdminStatus(
            @Parameter(description = "ID de l'administrateur", required = true)
            @PathVariable int id,
            @Parameter(description = "Actif (true) ou Inactif (false)", required = true)
            @RequestParam boolean actif){
        String message = adminService.toggleAdminStatus(id, actif);
        return ResponseEntity.ok(message);
    }
    @PostMapping(path = "/producteurs/ajouter")
    @Operation(
            summary = "Ajouter un producteur",
            description = "Permet à un administrateur d'ajouter un nouveau producteur. Le mot de passe sera encodé et le statut sera EN_ATTENTE."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producteur ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Producteur existe déjà"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Privilèges administrateur requis")
    })
    public ResponseEntity<Producteur> ajouterProducteur(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du nouveau producteur",
                    required = true
            )
            @RequestBody ProducteurRequestDTO producteurDTO) {
        return ResponseEntity.ok(adminService.createProducteur(producteurDTO));
    }
    @PutMapping("/producteurs/{id}/statut")
    @Operation(
            summary = "Changer le statut d'un producteur",
            description = "Permet à un administrateur d'accepter ou refuser un producteur"
    )
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    public ResponseEntity<Producteur> changerStatutProducteur(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int id,
            @Parameter(description = "Nouveau statut du producteur", required = true)
            @RequestParam StatutProducteur statut,
            @Parameter(description = "Raison du rejet (si statut = REFUSE)")
            @RequestParam(required = false) String raisonRejet) {
        return ResponseEntity.ok(adminService.changeProducteurStatut(id, statut, raisonRejet));
    }
    @GetMapping(path = "/producteurs")
    @Operation(
            summary = "Récupérer tous les producteurs",
            description = "Retourne la liste de tous les producteurs inscrit sur la plateforme"
    )
    @ApiResponse(responseCode = "200", description = "Liste des producteurs récupérée")
    public ResponseEntity<List<Producteur>> voirTousLesProducteurs() {
        return ResponseEntity.ok(adminService.recupererLesProducteurs());
    }
    @GetMapping(path = "/consommateurs")
    @Operation(
            summary = "Récupérer tous les consommateurs",
            description = "Retourne la liste de tous les consommateurs inscrits"
    )
    @ApiResponse(responseCode = "200", description = "Liste des consommateurs récupérée")
    public ResponseEntity<List<Consommateur>> voirTousLesConsommateurs() {
        return ResponseEntity.ok(adminService.recupererLesConsommateurs());
    }
    @GetMapping(path = "/produits")
    @Operation(
            summary = "Récupérer tous les produits",
            description = "Retourne la liste de tous les produits disponibles sur la plateforme"
    )
    @ApiResponse(responseCode = "200", description = "Liste des produits récupérée")
    public ResponseEntity<List<Produit>> voirTousLesProduits() {
        return ResponseEntity.ok(adminService.recupererTousLesProduits());
    }
    @GetMapping(path = "/commandes")
    @Operation(
            summary = "Récupérer toutes les commandes",
            description = "Retourne la liste de toutes les commandes passées"
    )
    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée")
    public ResponseEntity<List<Commande>> voirToutesLesCommandes() {
        return ResponseEntity.ok(adminService.recupererToutesLesCommandes());
    }
    @GetMapping("/paiements")
    @Operation(
            summary = "Récupérer tous les paiements",
            description = "Retourne la liste de tous les paiements effectués"
    )
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée")
    public ResponseEntity<List<Paiement>> voirTousLesPaiements() {
        return ResponseEntity.ok(adminService.recupererTousLesPaiements());
    }


}
