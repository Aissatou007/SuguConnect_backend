package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Service.ConsommateurService;
import odk.SuguConnect.Service.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller responsable de la gestion des paiements
 * Endpoints pour valider, consulter et gérer les paiements
 */
@RestController
@RequestMapping(path = "/paiement")
@RequiredArgsConstructor
@Tag(name = "Paiement", description = "API de gestion des paiements - Orange Money, Wave, Espèces")
public class PaiementController {
    private final PaiementService paiementService;
    private final ConsommateurService consommateurService;
    
    @GetMapping(path = "/test")
    public ResponseEntity<String> testEndpoint() {
        System.out.println("=== Test endpoint atteint ===");
        return ResponseEntity.ok("Test endpoint fonctionnel");
    }
    
    @PostMapping
    @Operation(
            summary = "Créer un paiement",
            description = "Créer un nouveau paiement"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paiement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de paiement invalides"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<Paiement> creerPaiement(
            @Parameter(description = "Données du paiement", required = true)
            @RequestBody Map<String, Object> paiementData,
            Authentication authentication) {
        
        System.out.println("=== Requête de création de paiement reçue ===");
        System.out.println("Données de paiement: " + paiementData);
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + authentication.getPrincipal());
        System.out.println("Authorities: " + authentication.getAuthorities());
        
        try {
            // Extraire les données du paiement
            int commandeId = (int) paiementData.get("commandeId");
            String methodePaiement = (String) paiementData.get("methodePaiement");
            double montant = ((Number) paiementData.get("montant")).doubleValue();
            String numeroTelephone = (String) paiementData.getOrDefault("numeroTelephone", null);
            
            System.out.println("Commande ID: " + commandeId);
            System.out.println("Méthode de paiement: " + methodePaiement);
            System.out.println("Montant: " + montant);
            System.out.println("Numéro de téléphone: " + numeroTelephone);
            
            // Extraire l'ID du consommateur
            Integer consommateurIdObj = (Integer) paiementData.get("consommateurId");
            int consommateurId;
            
            if (consommateurIdObj != null) {
                // Utiliser l'ID du consommateur fourni
                consommateurId = consommateurIdObj;
                System.out.println("Consommateur ID fourni: " + consommateurId);
            } else {
                // Extraire l'ID du consommateur depuis l'authentification
                String telephone = authentication.getName();
                System.out.println("Téléphone de l'utilisateur authentifié: " + telephone);
                
                Consommateur consommateur = consommateurService.findByTelephone(telephone);
                System.out.println("Consommateur trouvé: " + consommateur);
                consommateurId = consommateur.getId();
            }
            
            System.out.println("Consommateur ID final: " + consommateurId);
            
            // Créer le paiement via le service
            Paiement paiement = paiementService.creerPaiement(
                commandeId, 
                ModePaiement.valueOf(methodePaiement), 
                montant, 
                numeroTelephone,
                consommateurId
            );
            
            System.out.println("Paiement créé avec succès");
            return ResponseEntity.status(201).body(paiement);
        } catch (Exception e) {
            System.out.println("ERREUR lors de la création du paiement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    @Operation(
            summary = "Récupérer tous les paiements",
            description = "Retourne la liste de tous les paiements (Admin uniquement)"
    )
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée")
    public ResponseEntity<List<Paiement>> recupererTousLesPaiements() {
        List<Paiement> paiements = paiementService.recupererTousLesPaiements();
        return ResponseEntity.ok(paiements);
    }
    
    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer un paiement par ID",
            description = "Retourne les détails d'un paiement spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Paiement> recupererPaiementParId(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable int id) {
        Paiement paiement = paiementService.recupererPaiementParId(id);
        return ResponseEntity.ok(paiement);
    }
    
    @GetMapping(path = "/commande/{commandeId}")
    @Operation(
            summary = "Récupérer le paiement d'une commande",
            description = "Retourne le paiement associé à une commande"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé pour cette commande")
    })
    public ResponseEntity<Paiement> recupererPaiementParCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId) {
        Paiement paiement = paiementService.recupererPaiementParCommande(commandeId);
        return ResponseEntity.ok(paiement);
    }
    
    @PutMapping(path = "/{id}/valider")
    @Operation(
            summary = "Valider un paiement",
            description = "Marque un paiement comme validé (VALIDE). Utilisé pour les paiements en espèces ou après confirmation manuelle."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement validé avec succès"),
            @ApiResponse(responseCode = "400", description = "Paiement déjà validé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Paiement> validerPaiement(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable int id,
            @Parameter(description = "Référence de transaction (optionnel)")
            @RequestParam(required = false) String referenceTransaction) {
        
        Paiement paiement = paiementService.validerPaiement(id, referenceTransaction);
        return ResponseEntity.ok(paiement);
    }
    
    @PutMapping(path = "/{id}/echouer")
    @Operation(
            summary = "Marquer un paiement comme échoué",
            description = "Marque un paiement comme échoué (ECHOUE) et annule la commande associée"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement marqué comme échoué"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Paiement> marquerPaiementEchoue(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable int id,
            @Parameter(description = "Motif de l'échec", required = true)
            @RequestParam String motifEchec) {
        
        Paiement paiement = paiementService.marquerPaiementEchoue(id, motifEchec);
        return ResponseEntity.ok(paiement);
    }
    
    @PutMapping(path = "/{id}/rembourser")
    @Operation(
            summary = "Rembourser un paiement",
            description = "Effectue le remboursement d'un paiement validé (REMBOURSE). Envoie une notification au consommateur."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Remboursement effectué avec succès"),
            @ApiResponse(responseCode = "400", description = "Paiement ne peut pas être remboursé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Paiement> rembourserPaiement(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable int id,
            @Parameter(description = "Motif du remboursement", required = true)
            @RequestParam String motifRemboursement) {
        
        Paiement paiement = paiementService.rembourserPaiement(id, motifRemboursement);
        return ResponseEntity.ok(paiement);
    }
    
    @PostMapping(path = "/{id}/initier-mobile")
    @Operation(
            summary = "Initier un paiement mobile",
            description = "Initie un paiement via Orange Money ou Wave. Le consommateur recevra une notification push pour confirmer le paiement."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement initié avec succès"),
            @ApiResponse(responseCode = "400", description = "Paiement ne peut pas être initié"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Map<String, String>> initierPaiementMobile(
            @Parameter(description = "ID du paiement", required = true)
            @PathVariable int id,
            @Parameter(description = "Numéro de téléphone pour le paiement", required = true)
            @RequestParam String numeroTelephone) {
        
        String message = paiementService.initierPaiementMobile(id, numeroTelephone);
        return ResponseEntity.ok(Map.of(
            "message", message,
            "statut", "EN_ATTENTE",
            "paiementId", String.valueOf(id)
        ));
    }
    
    @PostMapping(path = "/webhook")
    @Operation(
            summary = "Webhook pour Orange Money / Wave",
            description = "Endpoint pour recevoir les notifications de paiement des fournisseurs (Orange Money, Wave)"
    )
    @ApiResponse(responseCode = "200", description = "Webhook traité avec succès")
    public ResponseEntity<String> webhookPaiement(
            @RequestBody Map<String, String> payload) {
        
        // Extraire les données du webhook
        String referenceTransaction = payload.get("reference");
        String statut = payload.get("status");
        int paiementId = Integer.parseInt(payload.getOrDefault("payment_id", "0"));
        
        // Traiter le webhook
        paiementService.traiterWebhookPaiement(referenceTransaction, statut, paiementId);
        
        return ResponseEntity.ok("Webhook traité avec succès");
    }
}