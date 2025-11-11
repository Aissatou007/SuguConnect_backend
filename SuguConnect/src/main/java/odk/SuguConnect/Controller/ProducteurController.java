package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Request.ProduitRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Service.CommandeService;
import odk.SuguConnect.Service.FileStorageService;
import odk.SuguConnect.Service.ProducteurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/producteur")
@RequiredArgsConstructor
@Tag(name = "Producteur", description = "API de gestion des producteurs et de leurs produits")
public class ProducteurController {
    private final ProducteurService producteurService;
    private final FileStorageService fileStorageService;
    private final CommandeService commandeService;
    private final odk.SuguConnect.Service.ProduitService produitService;  // Ajout du ProduitService pour respecter SRP

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
        String message = producteurService.inscriptionProducteur(producteurRequestDTO, producteurRequestDTO.telephone());
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

        // Sauvegarder les photos et obtenir les URLs
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String fileName = fileStorageService.storeFile(photo);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(fileName)
                        .toUriString();
                photoUrls.add(fileDownloadUri);
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

        // Créer le produit modifié
        Produit produitModifie = new Produit();
        produitModifie.setId(produitId);
        
        try {
            if (nom != null) produitModifie.setNom(nom);
            if (description != null) produitModifie.setDescription(description);
            if (prixUnitaire != null) produitModifie.setPrixUnitaire(Float.parseFloat(prixUnitaire));
            if (unite != null) produitModifie.setUnite(odk.SuguConnect.Enums.Unite.valueOf(unite.toUpperCase()));
            if (quantite != null) produitModifie.setQuantite(Integer.parseInt(quantite));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Format de données invalide: " + e.getMessage());
        }

        // Si de nouvelles photos sont fournies
        if (photos != null && !photos.isEmpty()) {
            if (photos.size() > 4) {
                return ResponseEntity.badRequest().body("Maximum 4 photos autorisées");
            }

            List<String> photoUrls = new ArrayList<>();
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String fileName = fileStorageService.storeFile(photo);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/")
                            .path(fileName)
                            .toUriString();
                    photoUrls.add(fileDownloadUri);
                }
            }
            produitModifie.setPhotos(photoUrls);
        }

        Produit produitModifieObj = produitService.modifierProduit(produitModifie, produitId, producteurId);
        return ResponseEntity.ok("Produit modifié avec succès, ID: " + produitModifieObj.getId());
    }

    @DeleteMapping(path = "/{producteurId}/produit/{produitId}")
    @Operation(
            summary = "Supprimer un produit",
            description = "Permet de supprimer un produit"
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

        String message = produitService.supprimerProduit(produitId, producteurId);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping(path = "/commande/{commandeId}/statut")
    @Operation(
            summary = "Changer le statut d'une commande",
            description = "Permet au producteur de changer le statut d'une commande (VALIDEE, REFUSEE, EN_LIVRAISON, LIVREE)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<Commande> changerStatutCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du producteur", required = true)
            @RequestParam int producteurId,
            @Parameter(description = "Nouveau statut (VALIDEE, REFUSEE, EN_LIVRAISON, LIVREE)", required = true)
            @RequestParam StatutCommande nouveauStatut,
            @Parameter(description = "Motif de rejet (obligatoire si REFUSEE)")
            @RequestParam(required = false) String motifRejet) {
        
        Commande commande = commandeService.changerStatutCommande(commandeId, producteurId, nouveauStatut, motifRejet);
        return ResponseEntity.ok(commande);
    }
}