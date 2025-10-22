package odk.SuguConnect.DTO.SimpleDTO;// PaiementController.java

import odk.SuguConnect.DTO.SimpleDTO.PaiementRecord;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    public ResponseEntity<PaiementRecord> createPaiement(@RequestBody CreatePaiementRequest request) {
        PaiementRecord paiement = paiementService.createPaiement(request);
        return ResponseEntity.ok(paiement);
    }

    @GetMapping
    public ResponseEntity<List<PaiementRecord>> getAllPaiements() {
        List<PaiementRecord> paiements = paiementService.getAllPaiements();
        return ResponseEntity.ok(paiements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementRecord> getPaiementById(@PathVariable Integer id) {
        PaiementRecord paiement = paiementService.getPaiementById(id);
        return ResponseEntity.ok(paiement);
    }

    @GetMapping("/consommateur/{consommateurId}")
    public ResponseEntity<List<PaiementRecord>> getPaiementsByConsommateur(@PathVariable Integer consommateurId) {
        List<PaiementRecord> paiements = paiementService.getPaiementsByConsommateur(consommateurId);
        return ResponseEntity.ok(paiements);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<PaiementRecord> updateStatutPaiement(
            @PathVariable Integer id,
            @RequestBody UpdatePaiementRequest request) {
        PaiementRecord paiement = paiementService.updateStatutPaiement(id, request);
        return ResponseEntity.ok(paiement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable Integer id) {
        paiementService.deletePaiement(id);
        return ResponseEntity.noContent().build();
    }
}