package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
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
}
