package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProducteurService {
    private final ProducteurRepository producteurRepository;
    private final PasswordEncoder passwordEncoder;

    public String inscriptionProducteur(ProducteurRequestDTO dto, String telephone) {
        if (producteurRepository.findByTelephone(telephone) != null) {
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
        }

        Producteur producteur = new Producteur();
        producteur.setNom(dto.getNom());
        producteur.setPrenom(dto.getPrenom());
        producteur.setTelephone(telephone);
        producteur.setEmail(dto.getEmail());
        producteur.setLocalisation(dto.getLocalisation());
        producteur.setLatitude(dto.getLatitude());
        producteur.setLongitude(dto.getLongitude());
        producteur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        producteur.setRole(Role.PRODUCTEUR);
        producteur.setStatutProducteur(StatutProducteur.EN_ATTENTE);
        producteur.setDescription(dto.getDescription());
        producteur.setNomFerme(dto.getNomFerme());
        producteur.setDateInscription(LocalDate.now());
        producteur.setActif(true);

        producteurRepository.save(producteur);
        return "Inscription réussie. Votre compte est en attente de validation par un administrateur.";
    }

    public List<ProducteurResponseDTO> recupererLesProducteurs() {
        List<Producteur> producteurs = producteurRepository.findAll();
        return producteurs.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProducteurResponseDTO recupererUnProducteur(int id) {
        Producteur producteur = producteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé"));
        return toResponseDTO(producteur);
    }

    public String modifierInformationProducteur(ProducteurRequestDTO dto, int id) {
        Producteur producteur = producteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé"));

        producteur.setNom(dto.getNom());
        producteur.setPrenom(dto.getPrenom());
        producteur.setTelephone(dto.getTelephone());
        producteur.setEmail(dto.getEmail());
        producteur.setLocalisation(dto.getLocalisation());
        producteur.setLatitude(dto.getLatitude());
        producteur.setLongitude(dto.getLongitude());
        producteur.setDescription(dto.getDescription());
        producteur.setNomFerme(dto.getNomFerme());

        producteurRepository.save(producteur);
        return "Informations mises à jour avec succès";
    }

    public String supprimerProducteur(int id) {
        Producteur producteur = producteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producteur non trouvé"));

        producteurRepository.delete(producteur);
        return "Compte supprimé avec succès";
    }

    public Producteur getProducteurById(int id) {
        return producteurRepository.findById(id).orElse(null);
    }

    private ProducteurResponseDTO toResponseDTO(Producteur producteur) {
        ProducteurResponseDTO dto = new ProducteurResponseDTO();
        dto.setId(producteur.getId());
        dto.setNom(producteur.getNom());
        dto.setPrenom(producteur.getPrenom());
        dto.setTelephone(producteur.getTelephone());
        dto.setEmail(producteur.getEmail());
        dto.setLocalisation(producteur.getLocalisation());
        dto.setLatitude(producteur.getLatitude());
        dto.setLongitude(producteur.getLongitude());
        dto.setRole(producteur.getRole());
        dto.setStatutProducteur(producteur.getStatutProducteur());
        dto.setDescription(producteur.getDescription());
        dto.setNomFerme(producteur.getNomFerme());
        dto.setPhotoUrl(producteur.getPhotoUrl());
        dto.setDateInscription(producteur.getDateInscription());
        dto.setMotifDeRejet(producteur.getMotifDeRejet());
        return dto;
    }
}