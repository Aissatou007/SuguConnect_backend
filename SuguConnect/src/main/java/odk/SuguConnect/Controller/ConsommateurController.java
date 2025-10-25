package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Request.PasserCommandeRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Mapper.CommandeMapper;
import odk.SuguConnect.Service.CommandeService;
import odk.SuguConnect.Service.ConsommateurService;
import odk.SuguConnect.Service.PanierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/consommateur")
@RequiredArgsConstructor
@Tag(name = "Consommateur", description = "API de gestion des consommateurs, paniers et commandes")
public class ConsommateurController {
    private final ConsommateurService consommateurService;
    private final CommandeService commandeService;
    private final PanierService panierService;

    @PostMapping(path = "/inscription")
    @Operation(
            summary = "Inscription d'un consommateur",
            description = "Permet à un consommateur de créer un compte sur la plateforme"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "400", description = "Compte existant ou données invalides")
    })
    public ResponseEntity<String> inscription(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du consommateur",
                    required = true
            )
            @RequestBody ConsommateurRequestDTO consommateurRequestDTO) {
        String message = consommateurService.inscriptionConsommateur(consommateurRequestDTO, consommateurRequestDTO.telephone());
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/consommateurs")
    @Operation(
            summary = "Récupérer tous les consommateurs",
            description = "Retourne la liste de tous les consommateurs inscrits"
    )
    @ApiResponse(responseCode = "200", description = "Liste des consommateurs récupérée")
    public ResponseEntity<List<ConsommateurResponseDTO>> recupererTousLesConsommateurs() {
        List<ConsommateurResponseDTO> consommateurs = consommateurService.recupererLesConsommateurs();
        return ResponseEntity.ok(consommateurs);
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer un consommateur par son ID",
            description = "Retourne les détails d'un consommateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consommateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Consommateur non trouvé")
    })
    public ResponseEntity<ConsommateurResponseDTO> recupererUnConsommateur(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int id) {
        ConsommateurResponseDTO consommateur = consommateurService.recupererUnConsommateur(id);
        return ResponseEntity.ok(consommateur);
    }

    @PutMapping(path = "/{id}")
    @Operation(
            summary = "Modifier un consommateur",
            description = "Permet de modifier les informations d'un consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consommateur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Consommateur non trouvé")
    })
    public ResponseEntity<String> modifier(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du consommateur"
            )
            @RequestBody ConsommateurRequestDTO consommateurRequestDTO) {
        String message = consommateurService.modifierInformationConsommateur(consommateurRequestDTO, id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Supprimer un consommateur",
            description = "Permet de supprimer un compte consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consommateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Consommateur non trouvé")
    })
    public ResponseEntity<String> supprimer(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int id) {
        String message = consommateurService.supprimerConsommateur(id);
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/produits")
    @Operation(
            summary = "Voir les produits disponibles",
            description = "Retourne tous les produits disponibles à l'achat"
    )
    @ApiResponse(responseCode = "200", description = "Liste des produits disponibles")
    public ResponseEntity<List<Produit>> voirProduitsDisponibles() {
        return ResponseEntity.ok(consommateurService.voirTousLesProduitsDisponibles());
    }
    
    @GetMapping(path = "/produits/recherche")
    @Operation(
            summary = "Rechercher des produits par nom",
            description = "Retourne les produits disponibles dont le nom contient le terme recherché"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvés"),
            @ApiResponse(responseCode = "400", description = "Terme de recherche invalide")
    })
    public ResponseEntity<List<Produit>> rechercherProduitsParNom(
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Produit> produits = consommateurService.filtrerProduitsDisponiblesParNom(nom);
        return ResponseEntity.ok(produits);
    }

    @PostMapping(path = "/{idConsommateur}/panier/ajouter/{idProduit}")
    @Operation(
            summary = "Ajouter un produit au panier",
            description = "Permet d'ajouter un produit au panier d'un consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit ajouté au panier"),
            @ApiResponse(responseCode = "400", description = "Stock insuffisant"),
            @ApiResponse(responseCode = "404", description = "Consommateur ou produit non trouvé")
    })
    public ResponseEntity<String> ajouterAuPanier(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int idProduit,
            @Parameter(description = "Quantité à ajouter", required = true)
            @RequestParam int quantite) {
        String message = panierService.ajouterProduitAuPanier(idConsommateur, idProduit, quantite);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{idConsommateur}/panier/retirer/{idProduit}")
    @Operation(
            summary = "Retirer un produit du panier",
            description = "Permet de retirer un produit du panier d'un consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit retiré du panier"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé dans le panier")
    })
    public ResponseEntity<String> retirerDuPanier(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int idProduit) {
        String message = panierService.retirerProduitDuPanier(idConsommateur, idProduit);
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/{idConsommateur}/panier")
    @Operation(
            summary = "Voir le panier",
            description = "Permet de consulter le contenu du panier d'un consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panier récupéré"),
            @ApiResponse(responseCode = "404", description = "Panier vide ou consommateur non trouvé")
    })
    public ResponseEntity<Panier> voirPanier(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur) {
        Panier panier = panierService.voirPanier(idConsommateur);
        return ResponseEntity.ok(panier);
    }

    @GetMapping(path = "/{idConsommateur}/commandes")
    @Operation(
            summary = "Voir les commandes du consommateur",
            description = "Retourne l'historique de toutes les commandes passées par un consommateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes récupérées"),
            @ApiResponse(responseCode = "404", description = "Aucune commande trouvée")
    })
    public ResponseEntity<List<odk.SuguConnect.DTO.Responses.CommandeResponseDTO>> voirMesCommandes(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur) {
        List<Commande> commandes = commandeService.voirCommandesParConsommateur(idConsommateur);
        List<odk.SuguConnect.DTO.Responses.CommandeResponseDTO> response = commandes.stream()
                .map(CommandeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/{idConsommateur}/commande")
    @Operation(
            summary = "Passer une commande (utilise le panier)",
            description = "Permet à un consommateur de passer une commande avec les produits de son panier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande passée avec succès"),
            @ApiResponse(responseCode = "400", description = "Panier vide ou stock insuffisant"),
            @ApiResponse(responseCode = "404", description = "Consommateur non trouvé")
    })
    public ResponseEntity<odk.SuguConnect.DTO.Responses.CommandeResponseDTO> passerCommande(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur,
            @Parameter(description = "Mode de paiement choisi", required = true)
            @RequestParam ModePaiement modePaiement) {
        Commande commande = commandeService.passerCommande(idConsommateur, modePaiement);
        return ResponseEntity.ok(CommandeMapper.toResponse(commande));
    }
    
    @PostMapping(path = "/{idConsommateur}/commande/direct")
    @Operation(
            summary = "Passer une commande directe",
            description = "Permet à un consommateur de passer une commande en choisissant directement les produits (sans utiliser le panier)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande passée avec succès"),
            @ApiResponse(responseCode = "400", description = "Stock insuffisant ou données invalides"),
            @ApiResponse(responseCode = "404", description = "Consommateur ou produit non trouvé")
    })
    public ResponseEntity<odk.SuguConnect.DTO.Responses.CommandeResponseDTO> passerCommandeDirecte(
            @Parameter(description = "ID du consommateur", required = true)
            @PathVariable int idConsommateur,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Produits à commander et mode de paiement"
            )
            @RequestBody PasserCommandeRequestDTO request) {
        Commande commande = commandeService.passerCommandeAvecProduits(idConsommateur, request);
        return ResponseEntity.ok(CommandeMapper.toResponse(commande));
    }
    
    @GetMapping(path = "/commande/{commandeId}")
    @Operation(
            summary = "Voir une commande spécifique",
            description = "Retourne les détails d'une commande spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande récupérée"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<odk.SuguConnect.DTO.Responses.CommandeResponseDTO> voirCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId) {
        Commande commande = commandeService.voirCommandeParId(commandeId);
        return ResponseEntity.ok(CommandeMapper.toResponse(commande));
    }
    
    @PostMapping(path = "/commande/{commandeId}/valider-reception")
    @Operation(
            summary = "Valider la réception d'une commande",
            description = "Permet au consommateur de confirmer qu'il a bien reçu sa commande. Cette validation est requise avant de pouvoir donner un avis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réception validée avec succès"),
            @ApiResponse(responseCode = "400", description = "Commande non livrée ou déjà validée"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<odk.SuguConnect.DTO.Responses.CommandeResponseDTO> validerReceptionCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du consommateur", required = true)
            @RequestParam int consommateurId) {
        
        Commande commande = commandeService.validerReceptionCommande(commandeId, consommateurId);
        return ResponseEntity.ok(CommandeMapper.toResponse(commande));
    }
}