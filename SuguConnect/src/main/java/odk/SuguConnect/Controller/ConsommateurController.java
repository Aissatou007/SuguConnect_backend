package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Service.ConsommateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/consommateur")
public class ConsommateurController {
    private ConsommateurService consommateurService;
    @PostMapping(path = "/inscription")
    public ResponseEntity<String> inscription(
            @RequestBody ConsommateurRequestDTO consommateurRequestDTO){
        String message = consommateurService.inscriptionConsommateur(consommateurRequestDTO, consommateurRequestDTO.telephone());
        return ResponseEntity.ok(message);
    }
    @GetMapping(path = "/consommateurs")
    public ResponseEntity<List<ConsommateurResponseDTO>> recupererTousLesConsommateurs(){
        List<ConsommateurResponseDTO> consommateurs = consommateurService.recupererLesConsommateurs();
        return ResponseEntity.ok(consommateurs);
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<ConsommateurResponseDTO> recupererUnConsommateur(@PathVariable int id){
        ConsommateurResponseDTO consommateur =  consommateurService.recupererUnConsommateur(id);
        return ResponseEntity.ok(consommateur);
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> modifier(@PathVariable int id ,
                                           @RequestBody ConsommateurRequestDTO consommateurRequestDTO){
        String message = consommateurService.modifierInformationConsommateur(consommateurRequestDTO , id);
        return ResponseEntity.ok(message);
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String>supprimer(@PathVariable int id){
    String message = consommateurService.supprimerConsommateur(id);
    return ResponseEntity.ok(message);
    }
    @GetMapping(path = "/produits")
    public ResponseEntity<List<Produit>> voirProduitsDisponibles() {
        return ResponseEntity.ok(consommateurService.voirTousLesProduitsDisponibles());
    }
    @PostMapping(path = "/{idConsommateur}/panier/ajouter/{idProduit}")
    public ResponseEntity<String> ajouterAuPanier(@PathVariable int idConsommateur,
                                                  @PathVariable int idProduit,
                                                  @RequestParam int quantite) {
        String message = consommateurService.ajouterProduitAuPanier(idConsommateur, idProduit, quantite);
        return ResponseEntity.ok(message);
    }
    @DeleteMapping(path = "/{idConsommateur}/panier/retirer/{idProduit}")
    public ResponseEntity<String> retirerDuPanier(@PathVariable int idConsommateur,
                                                  @PathVariable int idProduit) {
        String message = consommateurService.retirerProduitDuPanier(idConsommateur, idProduit);
        return ResponseEntity.ok(message);
    }
    @PostMapping(path = "/{idConsommateur}/commande")
    public ResponseEntity<Commande> passerCommande(@PathVariable int idConsommateur,
                                                   @RequestParam ModePaiement modePaiement) {
        Commande commande = consommateurService.passerCommande(idConsommateur, modePaiement);
        return ResponseEntity.ok(commande);
    }
}
