package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsommateurService {
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;
    private final PasswordEncoder passwordEncoder;

    public String inscriptionConsommateur(ConsommateurRequestDTO dto, String telephone) {
        if (consommateurRepository.findByTelephone(telephone) != null) {
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
        }

        Consommateur consommateur = new Consommateur();
        consommateur.setNom(dto.getNom());
        consommateur.setPrenom(dto.getPrenom());
        consommateur.setTelephone(telephone);
        consommateur.setEmail(dto.getEmail());
        consommateur.setLocalisation(dto.getLocalisation());
        consommateur.setLatitude(dto.getLatitude());
        consommateur.setLongitude(dto.getLongitude());
        consommateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        consommateur.setRole(Role.CONSOMMATEUR);
        consommateur.setDateInscription(LocalDate.now());
        consommateur.setActif(true);

        consommateurRepository.save(consommateur);
        return "Inscription réussie";
    }

    public List<ConsommateurResponseDTO> recupererLesConsommateurs() {
        List<Consommateur> consommateurs = consommateurRepository.findAll();
        return consommateurs.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ConsommateurResponseDTO recupererUnConsommateur(int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur non trouvé"));
        return toResponseDTO(consommateur);
    }

    public String modifierInformationConsommateur(ConsommateurRequestDTO dto, int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur non trouvé"));

        consommateur.setNom(dto.getNom());
        consommateur.setPrenom(dto.getPrenom());
        consommateur.setTelephone(dto.getTelephone());
        consommateur.setEmail(dto.getEmail());
        consommateur.setLocalisation(dto.getLocalisation());
        consommateur.setLatitude(dto.getLatitude());
        consommateur.setLongitude(dto.getLongitude());

        consommateurRepository.save(consommateur);
        return "Informations mises à jour avec succès";
    }

    public String supprimerConsommateur(int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consommateur non trouvé"));

        consommateurRepository.delete(consommateur);
        return "Compte supprimé avec succès";
    }

    public List<Produit> voirTousLesProduitsDisponibles() {
        return produitRepository.findAllByStockDisponibleGreaterThan(0);
    }

    public List<Produit> filtrerProduitsDisponiblesParNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    public Consommateur getConsommateurById(int id) {
        return consommateurRepository.findById(id).orElse(null);
    }

    private ConsommateurResponseDTO toResponseDTO(Consommateur consommateur) {
        ConsommateurResponseDTO dto = new ConsommateurResponseDTO();
        dto.setId(consommateur.getId());
        dto.setNom(consommateur.getNom());
        dto.setPrenom(consommateur.getPrenom());
        dto.setTelephone(consommateur.getTelephone());
        dto.setEmail(consommateur.getEmail());
        dto.setLocalisation(consommateur.getLocalisation());
        dto.setLatitude(consommateur.getLatitude());
        dto.setLongitude(consommateur.getLongitude());
        dto.setRole(consommateur.getRole());
        dto.setDateInscription(consommateur.getDateInscription());
        if (consommateur.getPanier() != null) {
            dto.setPanierId(consommateur.getPanier().getId());
        }
        return dto;
    }
}