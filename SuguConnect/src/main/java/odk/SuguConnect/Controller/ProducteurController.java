package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Request.ProduitRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.DTO.Responses.CommandeResponseDTO;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Mapper.CommandeMapper;
import odk.SuguConnect.Service.CommandeService;
import odk.SuguConnect.Service.FileStorageService;
import odk.SuguConnect.Service.ProducteurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/producteur")
@RequiredArgsConstructor
@Tag(name = "Producteur", description = "API de gestion des producteurs et de leurs produits")
public class ProducteurController {
    private final ProducteurService producteurService;
    private final FileStorageService fileStorageService;
    private final CommandeService commandeService;
    private final odk.SuguConnect.Service.ProduitService produitService;  // Ajout du ProduitService pour respecter SRP
    private final odk.SuguConnect.Security.JwtService jwtService; // Ajout du JwtService pour l'authentification

    @PostMapping(path = "/inscription")
    @Operation(
            summary = "Inscription d'un producteur",
            description = "Permet à un producteur de s'inscrire sur la plateforme. Son compte sera en attente de validation."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producteur inscrit avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou compte existant")
    })
    public ResponseEntity<String> inscription(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du producteur",
                    required = true
            )
            @RequestBody ProducteurRequestDTO producteurRequestDTO){
        String message = producteurService.inscriptionProducteur(producteurRequestDTO, producteurRequestDTO.getTelephone());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping(path ="/producteurs")
    @Operation(
            summary = "Récupérer tous les producteurs",
            description = "Retourne la liste de tous les producteurs"
    )
    @ApiResponse(responseCode = "200", description = "Liste des producteurs récupérée")
    public ResponseEntity<List<ProducteurResponseDTO>> recupererLesProducteurs(){
        List<ProducteurResponseDTO> producteurs = producteurService.recupererLesProducteurs();
        return ResponseEntity.ok(producteurs);
    }

    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Récupérer un producteur par son ID",
            description = "Retourne les détails d'un producteur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producteur trouvé"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<ProducteurResponseDTO> recupererUnProducteur(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int id){
        ProducteurResponseDTO producteur = producteurService.recupererUnProducteur(id);
        return ResponseEntity.ok(producteur);
    }

    @PutMapping(path = "/{id}")
    @Operation(
            summary = "Modifier un producteur",
            description = "Permet de modifier les informations d'un producteur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producteur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<String> modifier(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du producteur"
            )
            @RequestBody ProducteurRequestDTO producteurRequestDTO,
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int id){
        String message = producteurService.modifierInformationProducteur(producteurRequestDTO, id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Supprimer un producteur",
            description = "Permet de supprimer un compte producteur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producteur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<String> supprimer(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int id){
        String message = producteurService.supprimerProducteur(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping(path = "/{producteurId}/produit", consumes = {"multipart/form-data"})
    @Operation(
            summary = "Ajouter un produit avec photos",
            description = "Permet à un producteur d'ajouter un nouveau produit avec ses photos (1 à 4 photos). **IMPORTANT:** Pour ajouter plusieurs photos, sélectionnez le champ 'photos' plusieurs fois dans Swagger en cliquant sur 'Add string item' ou utilisez Postman/Bruno en ajoutant plusieurs fichiers avec la même clé 'photos'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit ajouté avec succès"),
            @ApiResponse(responseCode = "403", description = "Producteur non autorisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<String> ajouterProduit(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @RequestPart(value = "nom") String nom,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "prixUnitaire") String prixUnitaire,
            @RequestPart(value = "unite") String unite,
            @RequestPart(value = "quantite") String quantite,
            @RequestPart(value = "categorieId") String categorieId,
            @Parameter(
                description = "Photos du produit (minimum 1, maximum 4). Pour ajouter plusieurs fichiers, sélectionnez ce champ plusieurs fois.",
                required = true,
                content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "photos") List<MultipartFile> photos) {

        // Vérifier le nombre de photos
        if (photos == null || photos.isEmpty()) {
            return ResponseEntity.badRequest().body("Au moins une photo est requise");
        }
        if (photos.size() > 4) {
            return ResponseEntity.badRequest().body("Maximum 4 photos autorisées");
        }

        // Convertir les paramètres String en types appropriés
        float prixUnitaireFloat;
        int quantiteInt;
        int categorieIdInt;
        
        try {
            prixUnitaireFloat = Float.parseFloat(prixUnitaire);
            quantiteInt = Integer.parseInt(quantite);
            categorieIdInt = Integer.parseInt(categorieId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Format de nombre invalide");
        }

        // Sauvegarder les photos et ne stocker que les noms de fichiers
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String fileName = fileStorageService.storeFile(photo);
                photoUrls.add(fileName);
            }
        }

        // Créer le produit
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrixUnitaire(prixUnitaireFloat);
        produit.setUnite(odk.SuguConnect.Enums.Unite.valueOf(unite.toUpperCase()));
        produit.setQuantite(quantiteInt);
        produit.setPhotos(photoUrls);
        
        // Associer la catégorie
        Categorie categorie = new Categorie();
        categorie.setId(categorieIdInt);
        produit.setCategorie(categorie);

        // Ajouter le produit
        Produit produitAjoute = produitService.ajouterProduit(produit, producteurId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Produit ajouté avec succès avec " + photoUrls.size() + " photos, ID: " + produitAjoute.getId());
    }

    @GetMapping(path = "/{producteurId}/produit")
    @Operation(
            summary = "Lister les produits d'un producteur",
            description = "Retourne tous les produits d'un producteur spécifique"
    )
    @ApiResponse(responseCode = "200", description = "Liste des produits récupérée")
    public ResponseEntity<List<Produit>> listerProduits(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<Produit> produits = produitService.listerLesProduits(producteurId);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping(path = "/{producteurId}/produit/recherche")
    @Operation(
            summary = "Rechercher des produits d'un producteur par nom",
            description = "Retourne les produits d'un producteur dont le nom contient le terme recherché"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits trouvés"),
            @ApiResponse(responseCode = "400", description = "Terme de recherche invalide")
    })
    public ResponseEntity<List<Produit>> rechercherProduits(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Produit> produits = produitService.filtrerProduitsParNom(nom);
        // Filtrer pour ne retourner que les produits de ce producteur
        List<Produit> produitsDuProducteur = produits.stream()
                .filter(produit -> produit.getProducteur().getId() == producteurId)
                .toList();
        return ResponseEntity.ok(produitsDuProducteur);
    }

    @PutMapping(path = "/{producteurId}/produit/{produitId}", consumes = {"multipart/form-data"})
    @Operation(
            summary = "Modifier un produit avec photos",
            description = "Permet de modifier les informations d'un produit et ses photos. **IMPORTANT:** Pour ajouter plusieurs photos, sélectionnez le champ 'photos' plusieurs fois."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit modifié avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<String> modifierProduit(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int produitId,
            @RequestPart(value = "nom", required = false) String nom,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "prixUnitaire", required = false) String prixUnitaire,
            @RequestPart(value = "unite", required = false) String unite,
            @RequestPart(value = "quantite", required = false) String quantite,
            @Parameter(
                description = "Nouvelles photos du produit (optionnel, maximum 4). Pour plusieurs fichiers, sélectionnez ce champ plusieurs fois.",
                content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        // Récupérer le produit existant
        Produit produitExistant = produitService.getProduitById(produitId);
        if (produitExistant == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier que le produit appartient au producteur
        if (produitExistant.getProducteur().getId() != producteurId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à modifier ce produit");
        }

        // Mettre à jour les champs fournis
        if (nom != null && !nom.trim().isEmpty()) {
            produitExistant.setNom(nom);
        }
        if (description != null) {
            produitExistant.setDescription(description);
        }
        if (prixUnitaire != null && !prixUnitaire.trim().isEmpty()) {
            try {
                produitExistant.setPrixUnitaire(Float.parseFloat(prixUnitaire));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Format de prix invalide");
            }
        }
        if (unite != null && !unite.trim().isEmpty()) {
            try {
                produitExistant.setUnite(odk.SuguConnect.Enums.Unite.valueOf(unite.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Unité invalide");
            }
        }
        if (quantite != null && !quantite.trim().isEmpty()) {
            try {
                produitExistant.setQuantite(Integer.parseInt(quantite));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Format de quantité invalide");
            }
        }

        // Gérer les nouvelles photos si fournies
        if (photos != null && !photos.isEmpty()) {
            // Supprimer les anciennes photos
            for (String photoUrl : produitExistant.getPhotos()) {
                fileStorageService.deleteFile(photoUrl);
            }
            
            // Sauvegarder les nouvelles photos
            List<String> nouvellesPhotoUrls = new ArrayList<>();
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String fileName = fileStorageService.storeFile(photo);
                    nouvellesPhotoUrls.add(fileName);
                }
            }
            produitExistant.setPhotos(nouvellesPhotoUrls);
        }

        // Mettre à jour le produit
        Produit produitMisAJour = produitService.modifierProduit(produitExistant, produitId, producteurId);
        return ResponseEntity.ok("Produit mis à jour avec succès");
    }

    @DeleteMapping("/{producteurId}/produit/{produitId}")
    @Operation(
            summary = "Supprimer un produit",
            description = "Permet à un producteur de supprimer un de ses produits"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<String> supprimerProduit(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int produitId) {
        
        // Récupérer le produit existant
        Produit produitExistant = produitService.getProduitById(produitId);
        if (produitExistant == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier que le produit appartient au producteur
        if (produitExistant.getProducteur().getId() != producteurId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        // Supprimer les photos associées
        for (String photoUrl : produitExistant.getPhotos()) {
            fileStorageService.deleteFile(photoUrl);
        }

        // Supprimer le produit
        produitService.supprimerProduit(produitId, producteurId);
        return ResponseEntity.ok("Produit supprimé avec succès");
    }

    @GetMapping("/{producteurId}/commandes")
    @Operation(
            summary = "Récupérer toutes les commandes d'un producteur",
            description = "Permet à un producteur de récupérer toutes ses commandes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes récupérées"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<CommandeResponseDTO>> recupererCommandesProducteur(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "Statut des commandes (optionnel)", required = false)
            @RequestParam(required = false) StatutCommande statut,
            @Parameter(description = "Recherche (optionnel)", required = false)
            @RequestParam(required = false) String search,
            Authentication authentication) {
        
        // Vérifier que l'utilisateur authentifié est bien le producteur concerné ou un administrateur
        String telephone = authentication.getName();
        odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
        
        // Vérifier que l'utilisateur a le droit d'accéder à ces commandes
        if (!producteur.getRole().name().equals("ADMIN") && producteur.getId() != producteurId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Commande> commandes = commandeService.voirCommandesParProducteur(producteurId, statut, search);
        List<CommandeResponseDTO> response = commandes.stream()
                .map(CommandeMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{producteurId}/commandes/{commandeId}")
    @Operation(
            summary = "Récupérer une commande par son ID",
            description = "Permet à un producteur de récupérer les détails d'une commande spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande trouvée"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<CommandeResponseDTO> recupererCommande(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            Authentication authentication) {
        
        // Vérifier que l'utilisateur authentifié est bien le producteur concerné
        String telephone = authentication.getName();
        odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
        
        if (producteur.getId() != producteurId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Commande commande = commandeService.voirCommandeParId(commandeId);
        CommandeResponseDTO commandeResponseDTO = CommandeMapper.toResponse(commande);
        return ResponseEntity.ok(commandeResponseDTO);
    }

    @GetMapping("/test-security")
    public ResponseEntity<String> testSecurity(Authentication authentication, HttpServletRequest request) {
        System.out.println("=== Test endpoint security ===");
        System.out.println("Méthode HTTP: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());
        
        String telephone = authentication.getName();
        System.out.println("Téléphone: " + telephone);
        
        odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
        System.out.println("Producteur: " + (producteur != null ? producteur.getId() : "null"));
        
        if (producteur != null) {
            System.out.println("Rôle: " + producteur.getRole().name());
        }
        
        return ResponseEntity.ok("Accès autorisé pour " + telephone);
    }

    @GetMapping("/test-legacy")
    public ResponseEntity<String> testLegacyEndpoint(
            @RequestParam(required = false) Integer producteurId,
            @RequestParam(required = false) String statut,
            HttpServletRequest request) {
        System.out.println("=== Test endpoint legacy ===");
        System.out.println("Méthode HTTP: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Param producteurId: " + producteurId);
        System.out.println("Param statut: " + statut);
        
        return ResponseEntity.ok("Endpoint legacy accessible");
    }

    @PutMapping("/test-simple")
    public ResponseEntity<String> testSimplePut() {
        System.out.println("=== Test endpoint PUT simple ===");
        return ResponseEntity.ok("Test PUT réussi");
    }

    @PutMapping("/test-expedition")
    public ResponseEntity<String> testExpedition(
            @RequestParam int producteurId,
            @RequestParam int commandeId,
            @RequestParam StatutCommande nouveauStatut,
            @RequestParam(required = false) String motifRejet,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        System.out.println("=== Test endpoint expédition ===");
        System.out.println("Producteur ID: " + producteurId);
        System.out.println("Commande ID: " + commandeId);
        System.out.println("Nouveau statut: " + nouveauStatut);
        System.out.println("Motif rejet: " + motifRejet);
        
        String telephone = authentication.getName();
        System.out.println("Téléphone auth: " + telephone);
        
        odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
        System.out.println("Producteur trouvé: " + (producteur != null ? producteur.getId() : "null"));
        
        if (producteur != null) {
            System.out.println("Rôle producteur: " + producteur.getRole().name());
            System.out.println("Comparaison ID - Token: " + producteur.getId() + ", Param: " + producteurId);
            boolean autorise = producteur.getRole().name().equals("ADMIN") || producteur.getId() == producteurId;
            System.out.println("Autorisé: " + autorise);
        }
        
        return ResponseEntity.ok("Test expédition réussi");
    }

    @PutMapping("/commande/{commandeId}/statut")
    @Operation(
            summary = "Changer le statut d'une commande (endpoint legacy)",
            description = "Permet à un producteur de changer le statut d'une commande - Endpoint de compatibilité"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut changé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<Commande> changerStatutCommandeLegacy(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du producteur", required = true)
            @RequestParam int producteurId,
            @Parameter(description = "Nouveau statut (VALIDEE, REFUSEE, EN_LIVRAISON, LIVREE)", required = true)
            @RequestParam StatutCommande nouveauStatut,
            @Parameter(description = "Motif de rejet (obligatoire si REFUSEE)")
            @RequestParam(required = false) String motifRejet,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        System.out.println("=== Appel endpoint legacy ===");
        System.out.println("Commande ID: " + commandeId);
        System.out.println("Producteur ID (param): " + producteurId);
        System.out.println("Nouveau statut: " + nouveauStatut);
        System.out.println("Motif rejet: " + motifRejet);
        
        try {
            // Vérifier que l'utilisateur authentifié est bien le producteur concerné ou un administrateur
            String telephone = authentication.getName();
            odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
            
            System.out.println("Producteur trouvé: " + (producteur != null ? producteur.getId() : "null"));
            System.out.println("Téléphone auth: " + telephone);
            
            if (producteur == null) {
                System.out.println("ERREUR: Producteur non trouvé pour le téléphone: " + telephone);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Extraire l'ID utilisateur du token JWT
            String authHeader = httpRequest.getHeader("Authorization");
            String jwt = authHeader.substring(7); // Enlever "Bearer "
            Integer tokenUserId = jwtService.extractUserId(jwt);
            
            // Ajouter des logs pour le débogage
            System.out.println("DEBUG: ID utilisateur du token: " + tokenUserId);
            System.out.println("DEBUG: ID producteur du token: " + producteur.getId());
            System.out.println("DEBUG: Rôle du producteur: " + producteur.getRole().name());
            
            // Vérifier que l'utilisateur est autorisé à modifier cette commande
            // Soit c'est un admin, soit c'est le producteur concerné
            System.out.println("DEBUG: Vérification autorisation - Producteur ID token: " + producteur.getId() + ", Producteur ID paramètre: " + producteurId);
            if (!producteur.getRole().name().equals("ADMIN") && producteur.getId() != producteurId) {
                System.out.println("ERREUR: Producteur " + producteur.getId() + " tente d'accéder à la commande du producteur " + producteurId);
                System.out.println("DEBUG: Token user ID: " + tokenUserId + ", Paramètre producteur ID: " + producteurId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            System.out.println("DEBUG: Autorisation accordée, appel du service");
            Commande commande = commandeService.changerStatutCommande(commandeId, producteurId, nouveauStatut, motifRejet);
            System.out.println("DEBUG: Commande mise à jour avec succès");
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            System.out.println("=== Erreur endpoint legacy ===");
            System.out.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping({
            "/{producteurId}/commandes/{commandeId}/statut",  // nouvelle convention
            "/{producteurId}/commande/{commandeId}/statut"    // compatibilité legacy (sans 's')
    })
    @Operation(
            summary = "Changer le statut d'une commande",
            description = "Permet à un producteur de changer le statut d'une commande (VALIDEE, REFUSEE, EN_LIVRAISON, LIVREE)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut changé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<?> changerStatutCommande(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "Nouveau statut (VALIDEE, REFUSEE, EN_LIVRAISON, LIVREE)", required = true)
            @RequestParam StatutCommande nouveauStatut,
            @Parameter(description = "Motif de rejet (obligatoire si REFUSEE)")
            @RequestParam(required = false) String motifRejet,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        System.out.println("=== Appel méthode changerStatutCommande ===");
        System.out.println("Commande ID: " + commandeId);
        System.out.println("Producteur ID (path): " + producteurId);
        System.out.println("Nouveau statut: " + nouveauStatut);
        System.out.println("Motif rejet: " + motifRejet);
        
        try {
            // Vérifier que l'utilisateur authentifié est bien le producteur concerné ou un administrateur
            String telephone = authentication.getName();
            odk.SuguConnect.Entity.Producteur producteur = producteurService.findByTelephone(telephone);
            
            System.out.println("Producteur trouvé: " + (producteur != null ? producteur.getId() : "null"));
            System.out.println("Téléphone auth: " + telephone);
            
            if (producteur == null) {
                System.out.println("ERREUR: Producteur non trouvé pour le téléphone: " + telephone);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Extraire l'ID utilisateur du token JWT
            String authHeader = httpRequest.getHeader("Authorization");
            String jwt = authHeader.substring(7); // Enlever "Bearer "
            Integer tokenUserId = jwtService.extractUserId(jwt);
            
            // Ajouter des logs pour le débogage
            System.out.println("DEBUG: ID utilisateur du token: " + tokenUserId);
            System.out.println("DEBUG: ID producteur trouvé par téléphone: " + producteur.getId());
            System.out.println("DEBUG: Rôle du producteur: " + producteur.getRole().name());
            
            // Vérifier que l'utilisateur est autorisé à modifier cette commande
            // Soit c'est un admin, soit c'est le producteur concerné (vérifier avec l'ID du token)
            System.out.println("DEBUG: Vérification autorisation - Token user ID: " + tokenUserId + ", Producteur ID paramètre: " + producteurId);
            System.out.println("DEBUG: Producteur authentifié ID: " + producteur.getId());
            
            // Si l'ID du token est null, utiliser l'ID du producteur trouvé par téléphone
            Integer userIdToCheck = (tokenUserId != null) ? tokenUserId : producteur.getId();
            
            // Vérification stricte : le producteur authentifié doit correspondre au producteurId dans l'URL
            // OU être un admin
            boolean isAdmin = producteur.getRole().name().equals("ADMIN");
            boolean isAuthorized = userIdToCheck.equals(producteurId);
            
            System.out.println("DEBUG: isAdmin: " + isAdmin + ", isAuthorized: " + isAuthorized);
            System.out.println("DEBUG: Comparaison - userIdToCheck (" + userIdToCheck + ") == producteurId (" + producteurId + "): " + isAuthorized);
            
            if (!isAdmin && !isAuthorized) {
                System.out.println("ERREUR: Producteur " + userIdToCheck + " (authentifié) tente d'accéder à la commande du producteur " + producteurId);
                System.out.println("DEBUG: Token user ID: " + tokenUserId + ", Producteur ID paramètre: " + producteurId);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Vous n'êtes pas autorisé à modifier cette commande. Producteur authentifié: " + userIdToCheck + ", Producteur requis: " + producteurId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            System.out.println("DEBUG: Autorisation accordée, appel du service");
            Commande commande = commandeService.changerStatutCommande(commandeId, producteurId, nouveauStatut, motifRejet);
            System.out.println("DEBUG: Commande mise à jour avec succès");
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            System.out.println("=== Erreur dans changerStatutCommande ===");
            System.out.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}