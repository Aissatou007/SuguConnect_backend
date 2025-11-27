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
import odk.SuguConnect.DTO.Responses.VenteDTO;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Repository.ProduitRepository;
import odk.SuguConnect.Service.CommandeService;
import odk.SuguConnect.Service.FileStorageService;
import odk.SuguConnect.Service.LivreurService;
import odk.SuguConnect.Service.ProducteurService;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.persistence.EntityNotFoundException;
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
    private final LivreurService livreurService;
    private final odk.SuguConnect.Service.ProduitService produitService;  // Ajout du ProduitService pour respecter SRP
    private final ProduitRepository produitRepository;

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
            description = "Permet à un producteur d'ajouter un nouveau produit avec ses photos (1 à 4 photos). " +
                    "Le producteur peut définir un seuil d'alerte personnalisé pour recevoir une notification quand le stock atteint ce niveau. " +
                    "**IMPORTANT:** Pour ajouter plusieurs photos, sélectionnez le champ 'photos' plusieurs fois dans Swagger en cliquant sur 'Add string item' ou utilisez Postman/Bruno en ajoutant plusieurs fichiers avec la même clé 'photos'."
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
            @RequestPart(value = "estBio", required = false) String estBio,
            @RequestPart(value = "seuilAlerte", required = false) String seuilAlerte,
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
        int seuilAlerteInt = 10; // Valeur par défaut

        try {
            prixUnitaireFloat = Float.parseFloat(prixUnitaire);
            quantiteInt = Integer.parseInt(quantite);
            categorieIdInt = Integer.parseInt(categorieId);
            if (seuilAlerte != null && !seuilAlerte.trim().isEmpty()) {
                seuilAlerteInt = Integer.parseInt(seuilAlerte);
                if (seuilAlerteInt < 0) {
                    return ResponseEntity.badRequest().body("Le seuil d'alerte doit être positif ou nul");
                }
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Format de nombre invalide");
        }

        // Sauvegarder les photos et obtenir les URLs
        List<String> photoUrls = new ArrayList<>();
        try {
            for (MultipartFile photo : photos) {
                if (photo != null && !photo.isEmpty()) {
                    String fileName = fileStorageService.storeFile(photo);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/suguconnect/files/download/")
                            .path(fileName)
                            .toUriString();
                    photoUrls.add(fileDownloadUri);
                    System.out.println("Photo sauvegardée: " + fileName + " -> " + fileDownloadUri);
                } else {
                    System.out.println("Photo vide ignorée");
                }
            }
            
            // Vérifier qu'au moins une photo a été sauvegardée
            if (photoUrls.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucune photo valide n'a pu être sauvegardée. Veuillez vérifier que les fichiers ne sont pas vides.");
            }
            
            System.out.println("Nombre de photos sauvegardées: " + photoUrls.size());
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde des photos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la sauvegarde des photos: " + e.getMessage());
        }

        // Créer le produit
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrixUnitaire(prixUnitaireFloat);
        produit.setUnite(odk.SuguConnect.Enums.Unite.valueOf(unite.toUpperCase()));
        produit.setQuantite(quantiteInt);
        produit.setPhotos(photoUrls);
        
        System.out.println("Produit créé avec " + produit.getPhotos().size() + " photos");
        
        // Définir si le produit est bio (par défaut false si non fourni)
        if (estBio != null && !estBio.trim().isEmpty()) {
            produit.setEstBio(Boolean.parseBoolean(estBio));
        } else {
            produit.setEstBio(false);
        }

        // Définir le seuil d'alerte (par défaut 10 si non fourni)
        produit.setSeuilAlerte(seuilAlerteInt);

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

    @GetMapping(path = "/{producteurId}/produit/en-stock")
    @Operation(
            summary = "Lister les produits en stock",
            description = "Retourne tous les produits d'un producteur ayant un stock disponible supérieur à 0"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits en stock récupérée"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<Produit>> listerProduitsEnStock(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<Produit> produits = produitService.listerProduitsEnStock(producteurId);
        return ResponseEntity.ok(produits);
    }

    @GetMapping(path = "/{producteurId}/produit/stock-faible")
    @Operation(
            summary = "Lister les produits avec stock faible",
            description = "Retourne tous les produits d'un producteur dont le stock disponible est inférieur ou égal au seuil d'alerte mais supérieur à 0"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits avec stock faible récupérée"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<Produit>> listerProduitsStockFaible(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<Produit> produits = produitService.listerProduitsStockFaible(producteurId);
        return ResponseEntity.ok(produits);
    }

    @GetMapping(path = "/{producteurId}/produit/epuises")
    @Operation(
            summary = "Lister les produits épuisés",
            description = "Retourne tous les produits d'un producteur dont le stock disponible est égal à 0"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des produits épuisés récupérée"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<Produit>> listerProduitsEpuises(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {
        List<Produit> produits = produitService.listerProduitsEpuises(producteurId);
        return ResponseEntity.ok(produits);
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

    @GetMapping(path = "/{producteurId}/commandes")
    @Operation(
            summary = "Récupérer les commandes d'un producteur",
            description = "Retourne toutes les commandes contenant au moins un produit du producteur, avec tous les détails (produits, consommateur, paiement, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<Commande>> getCommandesParProducteur(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {

        // Vérifier que le producteur existe
        producteurService.recupererUnProducteur(producteurId);

        // Récupérer toutes les commandes liées aux produits du producteur
        List<Commande> commandes = commandeService.getCommandesParProducteur(producteurId);

        return ResponseEntity.ok(commandes);
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
            description = "Permet de modifier les informations d'un produit et ses photos. " +
                    "Le producteur peut modifier le seuil d'alerte pour recevoir une notification quand le stock atteint ce niveau. " +
                    "**IMPORTANT:** Pour ajouter plusieurs photos, sélectionnez le champ 'photos' plusieurs fois."
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
            @RequestPart(value = "seuilAlerte", required = false) String seuilAlerte,
            @Parameter(
                    description = "Nouvelles photos du produit (optionnel, maximum 4). Pour plusieurs fichiers, sélectionnez ce champ plusieurs fois.",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        // Récupérer le produit existant pour préserver les valeurs non modifiées
        Produit produitExistant = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));
        
        // Vérifier que le producteur est le propriétaire
        if (produitExistant.getProducteur().getId() != producteurId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vous n'êtes pas autorisé à modifier ce produit");
        }
        
        // Créer le produit modifié avec les valeurs existantes préservées
        Produit produitModifie = new Produit();
        produitModifie.setId(produitId);
        // Préserver la valeur existante de estBio par défaut
        produitModifie.setEstBio(produitExistant.isEstBio());
        // Préserver la valeur existante de seuilAlerte par défaut
        produitModifie.setSeuilAlerte(produitExistant.getSeuilAlerte());

        try {
            if (nom != null) produitModifie.setNom(nom);
            if (description != null) produitModifie.setDescription(description);
            if (prixUnitaire != null) produitModifie.setPrixUnitaire(Float.parseFloat(prixUnitaire));
            if (unite != null) produitModifie.setUnite(odk.SuguConnect.Enums.Unite.valueOf(unite.toUpperCase()));
            if (quantite != null) produitModifie.setQuantite(Integer.parseInt(quantite));
            // Mettre à jour estBio seulement si explicitement fourni
            if (estBio != null && !estBio.trim().isEmpty()) {
                produitModifie.setEstBio(Boolean.parseBoolean(estBio));
            }
            // Mettre à jour seuilAlerte seulement si explicitement fourni
            if (seuilAlerte != null && !seuilAlerte.trim().isEmpty()) {
                int seuilAlerteInt = Integer.parseInt(seuilAlerte);
                if (seuilAlerteInt < 0) {
                    return ResponseEntity.badRequest().body("Le seuil d'alerte doit être positif ou nul");
                }
                produitModifie.setSeuilAlerte(seuilAlerteInt);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Format de nombre invalide: " + e.getMessage());
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
            description = "Permet au producteur de changer le statut d'une commande. " +
                    "Statuts possibles : VALIDEE (accepter la commande), REFUSEE/DECLINEE (refuser avec motif), " +
                    "EN_LIVRAISON (commande en cours de livraison), LIVREE (commande livrée). " +
                    "Le motif de rejet est obligatoire si le statut est REFUSEE ou DECLINEE. " +
                    "Si le statut est LIVREE, le livreurId est obligatoire et le consommateur recevra une notification " +
                    "avec les informations du livreur et le prix de livraison."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @ApiResponse(responseCode = "403", description = "Non autorisé - Le producteur n'est pas propriétaire de cette commande"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<Commande> changerStatutCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du producteur", required = true)
            @RequestParam int producteurId,
            @Parameter(description = "Nouveau statut : VALIDEE (accepter), REFUSEE/DECLINEE (refuser), EN_LIVRAISON, LIVREE", required = true)
            @RequestParam StatutCommande nouveauStatut,
            @Parameter(description = "Motif de rejet (obligatoire si statut = REFUSEE ou DECLINEE)")
            @RequestParam(required = false) String motifRejet,
            @Parameter(description = "ID du livreur (obligatoire si statut = LIVREE)")
            @RequestParam(required = false) Integer livreurId,
            @Parameter(description = "Prix de la livraison (optionnel, calculé automatiquement si non fourni)")
            @RequestParam(required = false) Double prixLivraison) {

        Commande commande = commandeService.changerStatutCommande(commandeId, producteurId, nouveauStatut, motifRejet, livreurId, prixLivraison);
        return ResponseEntity.ok(commande);
    }

    @GetMapping(path = "/{producteurId}/livreurs/disponibles")
    @Operation(
            summary = "Récupérer les livreurs disponibles",
            description = "Retourne la liste des livreurs disponibles pour la livraison des commandes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des livreurs disponibles récupérée"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<List<LivreurResponseDTO>> getLivreursDisponibles(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId) {

        // Vérifier que le producteur existe
        producteurService.recupererUnProducteur(producteurId);

        // Récupérer les livreurs disponibles
        List<LivreurResponseDTO> livreurs = livreurService.recupererLivreursDisponibles();

        return ResponseEntity.ok(livreurs);
    }

    @PutMapping(path = "/commande/{commandeId}/livreur")
    @Operation(
            summary = "Assigner un livreur à une commande",
            description = "Permet au producteur d'assigner un livreur disponible à une commande pour la livraison. " +
                    "Le consommateur recevra une notification avec les informations du livreur et le prix de livraison."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livreur assigné avec succès"),
            @ApiResponse(responseCode = "400", description = "Livreur non disponible"),
            @ApiResponse(responseCode = "403", description = "Non autorisé - Le producteur n'est pas propriétaire de cette commande"),
            @ApiResponse(responseCode = "404", description = "Commande ou livreur non trouvé")
    })
    public ResponseEntity<Commande> assignerLivreur(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable int commandeId,
            @Parameter(description = "ID du producteur", required = true)
            @RequestParam int producteurId,
            @Parameter(description = "ID du livreur à assigner", required = true)
            @RequestParam int livreurId,
            @Parameter(description = "Prix de la livraison (optionnel, calculé automatiquement si non fourni)")
            @RequestParam(required = false) Double prixLivraison) {

        Commande commande = commandeService.assignerLivreurACommande(commandeId, producteurId, livreurId, prixLivraison);
        return ResponseEntity.ok(commande);
    }

    @PostMapping(path = "/{producteurId}/photo-profil", consumes = {"multipart/form-data"})
    @Operation(
            summary = "Ajouter ou modifier la photo de profil",
            description = "Permet à un producteur d'uploader ou de modifier sa photo de profil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo de profil mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide ou vide"),
            @ApiResponse(responseCode = "403", description = "Non autorisé"),
            @ApiResponse(responseCode = "404", description = "Producteur non trouvé")
    })
    public ResponseEntity<Map<String, String>> uploadPhotoProfil(
            @Parameter(description = "ID du producteur", required = true)
            @PathVariable int producteurId,
            @Parameter(
                    description = "Photo de profil du producteur",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "photo") MultipartFile photo) {
        
        if (photo == null || photo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Photo requise"));
        }

        try {
            // Sauvegarder la photo
            String fileName = fileStorageService.storeFile(photo);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/suguconnect/files/download/")
                    .path(fileName)
                    .toUriString();

            // Mettre à jour la photo de profil du producteur
            producteurService.mettreAJourPhotoProfil(producteurId, fileDownloadUri);

            return ResponseEntity.ok(Map.of(
                    "message", "Photo de profil mise à jour avec succès",
                    "photoUrl", fileDownloadUri,
                    "producteurId", String.valueOf(producteurId)
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload de la photo: " + e.getMessage()));
        }
    }
}