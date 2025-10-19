package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Service.ProducteurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/producteur")
public class ProducteurController {
    private final ProducteurService producteurService ;

    public ProducteurController(ProducteurService producteurService) {
        this.producteurService = producteurService;
    }

    @PostMapping(path = "/inscription")
    public ResponseEntity<String> inscription(
            @RequestBody ProducteurRequestDTO producteurRequestDTO){
        String message = producteurService.inscriptionProducteur(producteurRequestDTO, producteurRequestDTO.telephone());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    @PostMapping(path = "/connexion")
    public ResponseEntity<ProducteurResponseDTO> connexion(@RequestBody Map<String , String> connexionRequesy){
        String telephone = connexionRequesy.get("telephone");
        String motDePasse = connexionRequesy.get("motDePasse");
        if(telephone == null || motDePasse == null){
            return ResponseEntity.badRequest().build();
        }
        ProducteurResponseDTO producteurResponseDTO = producteurService.connexionProducteur(telephone , motDePasse);
        return ResponseEntity.ok(producteurResponseDTO);
    }
    @GetMapping(path ="/producteurs" )
    public ResponseEntity<List<ProducteurResponseDTO>> recuprerLesProducteurs(){
        List<ProducteurResponseDTO> producteurs = producteurService.recupererLesProducteurs();
        return ResponseEntity.ok(producteurs);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProducteurResponseDTO> recupererUnProducteur(@PathVariable int id){
        ProducteurResponseDTO producteur = producteurService.recupererUnProducteur(id);
        return ResponseEntity.ok(producteur);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<String> modifier(@RequestBody ProducteurRequestDTO producteurRequestDTO
                                           ,@PathVariable int id ){
        String message = producteurService.modifierInformationProducteur(producteurRequestDTO , id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> supprimer(@PathVariable int id){
        String message = producteurService.supprimerProducteur(id);
        return ResponseEntity.ok(message);
    }
    @PostMapping(path = "/{producteurId}/produit")
    public ResponseEntity<String> ajouterProduit(
            @RequestBody Produit produit,
            @PathVariable int producteurId) {

        String message = producteurService.ajouterProduit(produit, producteurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    @GetMapping(path = "/{producteurId}/produit")
    public ResponseEntity<List<Produit>> listerProduits(@PathVariable int producteurId) {
        List<Produit> produits = producteurService.listerLesProduits(producteurId);
        return ResponseEntity.ok(produits);
    }

    @PutMapping(path = "/{producteurId}/produit/{produitId}")
    public ResponseEntity<String> modifierProduit(
            @RequestBody Produit produitModifie,
            @PathVariable int producteurId,
            @PathVariable int produitId) {

        String message = producteurService.modifierProduit(produitModifie, produitId, producteurId);
        return ResponseEntity.ok(message);
    }
    @DeleteMapping(path = "/{producteurId}/produit/{produitId}")
    public ResponseEntity<String> supprimerProduit(
            @PathVariable int producteurId,
            @PathVariable int produitId) {

        String message = producteurService.supprimerProduit(produitId, producteurId);
        return ResponseEntity.ok(message);
    }
}
