package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Service.ProducteurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/producteur")
public class ProducteurController {
    private ProducteurService producteurService ;
    @PostMapping(path = "/inscription")
    public ResponseEntity<String> inscription(
            @RequestBody ProducteurRequestDTO producteurRequestDTO){
        String message = producteurService.inscriptionProducteur(producteurRequestDTO, producteurRequestDTO.telephone());
        return ResponseEntity.ok(message);
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
}
