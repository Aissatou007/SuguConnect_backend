package odk.SuguConnect.DTO.SimpleDTO;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ConsommateurRepository consommateurRepository;

    // Convert Entity to DTO
    private PaiementRecord toRecord(Paiement paiement) {
        return new PaiementRecord(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getMethodePaiement(),
                paiement.getStatutPaiement(),
                paiement.getConsommateur() != null ? paiement.getConsommateur().getId() : null
        );
    }

    // Convert DTO to Entity
    private Paiement toEntity(CreatePaiementRequest request) {
        Paiement paiement = new Paiement();
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setMethodePaiement(request.methodePaiement());
        paiement.setStatutPaiement(StatutPaiement.EN_ATTENTE);

        // Set consommateur if provided
        if (request.consommateurId() != null) {
            Consommateur consommateur = consommateurRepository.findById(request.consommateurId())
                    .orElseThrow(() -> new RuntimeException("Consommateur non trouvé"));
            paiement.setConsommateur(consommateur);
        }

        return paiement;
    }

    @Transactional
    public PaiementRecord createPaiement(CreatePaiementRequest request) {
        Paiement paiement = toEntity(request);
        Paiement savedPaiement = paiementRepository.save(paiement);
        return toRecord(savedPaiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementRecord> getAllPaiements() {
        return paiementRepository.findAll()
                .stream()
                .map(this::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaiementRecord getPaiementById(Integer id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        return toRecord(paiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementRecord> getPaiementsByConsommateur(Integer consommateurId) {
        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(() -> new RuntimeException("Consommateur non trouvé"));
        return paiementRepository.findByConsommateur(consommateur)
                .stream()
                .map(this::toRecord)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaiementRecord updateStatutPaiement(Integer id, UpdatePaiementRequest request) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        paiement.setStatutPaiement(request.statutPaiement());
        Paiement updatedPaiement = paiementRepository.save(paiement);

        return toRecord(updatedPaiement);
    }

    @Transactional
    public void deletePaiement(Integer id) {
        if (!paiementRepository.existsById(id)) {
            throw new RuntimeException("Paiement non trouvé");
        }
        paiementRepository.deleteById(id);
    }
}