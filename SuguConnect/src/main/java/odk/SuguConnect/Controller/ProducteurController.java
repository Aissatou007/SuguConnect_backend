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
import odk.SuguConnect.DTO.Request.ProduitCreationRequestDTO;
import odk.SuguConnect.DTO.Request.ProduitRequestDTO;
import odk.SuguConnect.DTO.Responses.*;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Mapper.CommandeMapper;
import odk.SuguConnect.Mapper.ProduitMapper;
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
    private final odk.SuguConnect.Service.PaiementService paiementService;  // Ajout du PaiementService
    
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
            @RequestPart(value = "estBio") String estBio, // Now required
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
        
        // Vérifier que estBio est fourni
        if (estBio == null || estBio.isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'estBio' est obligatoire");
        }

        // Convertir les paramètres String en types appropriés
        float prixUnitaireFloat;
        int quantiteInt;
        int categorieIdInt;
        boolean estBioBool;

        try {
            prixUnitaireFloat = Float.parseFloat(prixUnitaire);
            quantiteInt = Integer.parseInt(quantite);
            categorieIdInt = Integer.parseInt(categorieId);
            estBioBool = Boolean.parseBoolean(estBio);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Format de nombre invalide: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Valeur invalide pour 'estBio'. Utilisez 'true' ou 'false'.");
        }

        // Sauvegarder les photos et obtenir les URLs
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String fileName = fileStorageService.storeFile(photo);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/suguconnect/files/download/")
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
        produit.setEstBio(estBioBool); // Définir si le produit est bio
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
    
    @PostMapping(path = "/{producteurId}/produit/ameliore", consumes = {"application/json"})
    @Operation(
            summary = "Ajouter un produit - Processus amélioré",
            description = "Permet à un producteur d'ajouter un nouveau produit avec un processus amélioré : choisir d'abord la catégorie, puis sélectionner ou entrer le nom du produit"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit ajouté avec succès"),
            @ApiResponse(responseCode = "403", description = "Producteur non autorisé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<String> ajouterProduitAmeliore(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du produit à ajouter"
            )
            @RequestBody ProduitCreationRequestDTO produitRequest) {

        // Créer le produit
        Produit produit = new Produit();
        produit.setNom(produitRequest.nom());
        produit.setDescription(produitRequest.description());
        produit.setPrixUnitaire(produitRequest.prixUnitaire());
        produit.setUnite(produitRequest.unite());
        produit.setQuantite(produitRequest.stockDisponible());
        produit.setEstBio(produitRequest.estBio());
        produit.setPhotos(produitRequest.photos());

        // Ajouter le produit avec le processus amélioré
        Produit produitAjoute = produitService.ajouterProduitAmeliore(produit, producteurId, produitRequest.categorieId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Produit ajouté avec succès avec " + produitRequest.photos().size() + " photos, ID: " + produitAjoute.getId());
    }
    
    @GetMapping(path = "/{producteurId}/categorie/{categorieId}/produits-existants")
    @Operation(
            summary = "Récupérer les produits existants dans une catégorie",
            description = "Permet à un producteur de voir les produits existants dans une catégorie pour faciliter la création de nouveaux produits"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<ProduitsParCategorieDTO> getProduitsParCategorie(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int categorieId) {
        
        ProduitsParCategorieDTO produitsParCategorie = produitService.getProduitsParCategorie(categorieId);
        return ResponseEntity.ok(produitsParCategorie);
    }
    
    @GetMapping(path = "/{producteurId}/categorie/{categorieId}/mes-produits")
    @Operation(
            summary = "Récupérer mes produits dans une catégorie",
            description = "Permet à un producteur de voir ses propres produits existants dans une catégorie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits récupérée"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<List<ProduitSimpleDTO>> getMesProduitsParCategorie(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "ID de la catégorie", required = true)
            @PathVariable int categorieId) {
        
        List<ProduitSimpleDTO> mesProduits = produitService.getProduitsDuProducteurParCategorie(producteurId, categorieId);
        return ResponseEntity.ok(mesProduits);
    }
    
    @GetMapping(path = "/{producteurId}/produit")
    @Operation(
            summary = "Lister les produits d'un producteur",
            description = "Retourne tous les produits d'un producteur spécifique"
    )
    @ApiResponse(responseCode = "200", description = "Liste des produits récupérée")
    public ResponseEntity<List<ProduitResponseDTO>> listerProduits(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<Produit> produits = produitService.listerLesProduits(producteurId);
        List<ProduitResponseDTO> produitDtos = produits.stream()
                .map(ProduitMapper::toDto)
                .toList();
        return ResponseEntity.ok(produitDtos);
    }
    
    @GetMapping(path = "/{producteurId}/commandes")
    @Operation(
            summary = "Lister toutes les commandes d'un producteur",
            description = "Retourne toutes les commandes contenant des produits de ce producteur"
    )
    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée")
    public ResponseEntity<List<CommandeResponseDTO>> listerCommandes(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "Filtrer par statut de commande (optionnel)")
            @RequestParam(required = false) StatutCommande statut) {
        List<Commande> commandes = commandeService.getCommandesParProducteur(producteurId, statut);
        List<CommandeResponseDTO> commandeDtos = commandes.stream()
                .map(CommandeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(commandeDtos);
    }
    
    @GetMapping(path = "/{producteurId}/paiements")
    @Operation(
            summary = "Lister tous les paiements reçus par un producteur",
            description = "Retourne tous les paiements des commandes contenant des produits de ce producteur"
    )
    @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée")
    public ResponseEntity<List<PaiementSimpleDTO>> listerPaiements(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "Filtrer par statut de paiement (optionnel)")
            @RequestParam(required = false) StatutPaiement statut) {
        List<Paiement> paiements = paiementService.getPaiementsRecusParProducteur(producteurId, statut);
        List<PaiementSimpleDTO> paiementDtos = paiements.stream()
                .map(paiement -> new PaiementSimpleDTO(
                        paiement.getIdPaiement(),
                        paiement.getMontant(),
                        paiement.getDatePaiement(),
                        paiement.getMethodePaiement(),
                        paiement.getStatutPaiement()
                ))
                .toList();
        return ResponseEntity.ok(paiementDtos);
    }
    
    @GetMapping(path = "/{producteurId}/ventes")
    @Operation(
            summary = "Historique des ventes d'un producteur",
            description = "Retourne l'historique des ventes pour tous les produits du producteur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<VenteDTO>> getHistoriqueVentes(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {

        // Vérifier que le producteur existe
        ProducteurResponseDTO producteur = producteurService.recupererUnProducteur(producteurId);

        // Récupérer toutes les commandes liées aux produits du producteur
        List<Commande> commandes = commandeService.getCommandesParProducteur(producteurId);

        // Construire la liste des ventes
        List<VenteDTO> ventes = new ArrayList<>();
        for (Commande commande : commandes) {
            commande.getCommandeProduits().forEach(cp -> {
                if (cp.getProduit().getProducteur().getId() == producteurId) {
                    ventes.add(new VenteDTO(
                            commande.getDateCommande().toString(),
                            cp.getProduit().getNom(),
                            cp.getQuantite(),
                            cp.getQuantite() * cp.getProduit().getPrixUnitaire()
                    ));
                }
            });
        }

        return ResponseEntity.ok(ventes);
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
    public ResponseEntity<List<ProduitResponseDTO>> rechercherProduits(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Produit> produits = produitService.filtrerProduitsParNom(nom);
        // Filtrer pour ne retourner que les produits de ce producteur
        List<ProduitResponseDTO> produitsDuProducteur = produits.stream()
                .filter(produit -> produit.getProducteur().getId() == producteurId)
                .map(ProduitMapper::toDto)
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
            @RequestPart(value = "estBio", required = false) String estBio,
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
            if (estBio != null && !estBio.isEmpty()) produitModifie.setEstBio(Boolean.parseBoolean(estBio));
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
                            .path("/suguconnect/files/download/")
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
    
    // Endpoint pour supprimer tous les produits - utilisé uniquement pour les tests
    @DeleteMapping(path = "/test/supprimer-tous-produits")
    @Operation(
            summary = "Supprimer tous les produits (TEST SEULEMENT)",
            description = "Permet de supprimer tous les produits de la base de données. UTILISÉ UNIQUEMENT POUR LES TESTS !",
            hidden = true
    )
    public ResponseEntity<String> supprimerTousLesProduitsPourTest() {
        String message = produitService.supprimerTousLesProduits();
        return ResponseEntity.ok(message);
    }
}