package odk.SuguConnect.Controller;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Service.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/categorie")
public class CategorieController {
    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }
    @PreAuthorize("hasrole('ADMIN')")
    @PostMapping
    public ResponseEntity<Categorie> creerCategorie(@RequestBody Categorie categorie){
        Categorie categorie1 = categorieService.creerCategorie(categorie);
        return ResponseEntity.ok(categorie1);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> supprimerCategorie(@PathVariable int id) {
        return ResponseEntity.ok(categorieService.supprimerCategorie(id));
    }
    @GetMapping
    public ResponseEntity<List<Categorie>> toutesLesCategories() {
        return ResponseEntity.ok(categorieService.listerCategorie());
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Categorie> categorieParId(@PathVariable int id) {
      return ResponseEntity.ok(categorieService.recupererUneCategorie(id));
    }

    @GetMapping(path = "/{id}/produits")
    public ResponseEntity<List<Produit>> produitsParCategorie(@PathVariable int id) {
       return ResponseEntity.ok(categorieService.listProduitParCategorie(id));
    }
}
