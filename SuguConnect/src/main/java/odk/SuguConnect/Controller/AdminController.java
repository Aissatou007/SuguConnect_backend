package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private AdminService adminService;

    @PostMapping(path = "/inscription")
    public ResponseEntity<AdminResponseDTO> inscription(
            @RequestBody AdminRequestDTO adminRequestDTO){
        AdminResponseDTO admin = adminService.createAdmin(adminRequestDTO);
        return ResponseEntity.ok(admin);
    }

    @GetMapping(path = "/admins")
    public ResponseEntity<List<AdminResponseDTO>> recupererTousLesAdmins(){
        List<AdminResponseDTO> admins = adminService.recupererLesAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AdminResponseDTO> recupererUnAdmin(@PathVariable int id){
        AdminResponseDTO admin =  adminService.recupererUnAdmin(id);
        return ResponseEntity.ok(admin);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<String> modifier(@PathVariable int id ,
                                           @RequestBody AdminRequestDTO adminRequestDTO){
        String message = adminService.modifierInformationAdmin(adminRequestDTO , id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> supprimer(@PathVariable int id){
        String message = adminService.supprimerAdmin(id);
        return ResponseEntity.ok(message);
    }
    @PostMapping(path = "/producteurs/ajouter")
    public ResponseEntity<Producteur> ajouterProducteur(@RequestBody Producteur producteur) {
        return ResponseEntity.ok(adminService.createProducteur(producteur));
    }
    @PutMapping("/producteurs/{id}/statut")
    public ResponseEntity<Producteur> changerStatutProducteur(@PathVariable int id,
                                                              @RequestParam StatutProducteur statut,
                                                              @RequestParam(required = false) String raisonRejet) {
        return ResponseEntity.ok(adminService.changeProducteurStatut(id, statut, raisonRejet));
    }
    @GetMapping(path = "/producteurs")
    public ResponseEntity<List<Producteur>> voirTousLesProducteurs() {
        return ResponseEntity.ok(adminService.recupererLesProducteurs());
    }
    @GetMapping(path = "/consommateurs")
    public ResponseEntity<List<Consommateur>> voirTousLesConsommateurs() {
        return ResponseEntity.ok(adminService.recupererLesConsommateurs());
    }
    @GetMapping(path = "/produits")
    public ResponseEntity<List<Produit>> voirTousLesProduits() {
        return ResponseEntity.ok(adminService.recupererTousLesProduits());
    }
    @GetMapping(path = "/commandes")
    public ResponseEntity<List<Commande>> voirToutesLesCommandes() {
        return ResponseEntity.ok(adminService.recupererToutesLesCommandes());
    }
    @GetMapping("/paiements")
    public ResponseEntity<List<Paiement>> voirTousLesPaiements() {
        return ResponseEntity.ok(adminService.recupererTousLesPaiements());
    }


}
