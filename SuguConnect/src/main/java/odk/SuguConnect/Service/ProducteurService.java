package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.ProducteurResponseDTO;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Mapper.ProducteurMapper;
import odk.SuguConnect.Repository.ProducteurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducteurService {
    private final ProducteurRepository producteurRepository;
    private final PasswordEncoder passwordEncoder;


    public String inscriptionProducteur(ProducteurRequestDTO dto, String telephone) {
        verifierCompteNonExistant(telephone);
        
        Producteur producteur = creerProducteur(dto);
        producteurRepository.save(producteur);
        
        return "Soyez le bienvenue ";
    }
    
    //Récupérer tous les producteurs
    public List<ProducteurResponseDTO> recupererLesProducteurs() {
        return producteurRepository.findAll().stream()
                .map(ProducteurMapper::toResponse)
                .toList();
    }

    public ProducteurResponseDTO recupererUnProducteur(int id) {
        Producteur producteur = findProducteurById(id);
        return ProducteurMapper.toResponse(producteur);
    }
    
    //Modifier les informations d'un producteur

    public String modifierInformationProducteur(ProducteurRequestDTO dto, int id) {
        Producteur producteur = findProducteurById(id);
        mettreAJourInformations(producteur, dto);
        producteurRepository.save(producteur);
        return "Vos informations ont été modifiées avec succès";
    }
    
    // Supprimer un producteur
    public String supprimerProducteur(int id) {
        Producteur producteur = findProducteurById(id);
        producteurRepository.delete(producteur);
        return "Le compte a été supprimé avec succès";
    }

    private void verifierCompteNonExistant(String telephone) {
        if (producteurRepository.findByTelephone(telephone) != null) {
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
    }
    
    private Producteur creerProducteur(ProducteurRequestDTO dto) {
        // Valider les noms avant création
        validerNoms(dto.nom(), dto.prenom());
        
        Producteur producteur = ProducteurMapper.toEntity(dto, new Producteur());
        producteur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        producteur.setRole(Role.PRODUCTEUR);
        producteur.setStatutProducteur(StatutProducteur.EN_ATTENTE);
        producteur.setDateInscription(LocalDate.now());
        producteur.setActif(true);
        return producteur;
    }
    
    private Producteur findProducteurById(int id) {
        return producteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce producteur n'a pas de compte"));
    }
    
    private void mettreAJourInformations(Producteur producteur, ProducteurRequestDTO dto) {
        // Valider les noms avant mise à jour
        validerNoms(dto.nom(), dto.prenom());
        
        producteur.setNom(dto.nom());
        producteur.setPrenom(dto.prenom());
        producteur.setTelephone(dto.telephone());
        producteur.setEmail(dto.email());
        producteur.setLocalisation(dto.localisation());
        producteur.setDescription(dto.description());

        if (dto.motDePasse() != null && !dto.motDePasse().isEmpty()) {
            producteur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        }
    }

    private void validerNoms(String nom, String prenom) {
        // Vérifier que les noms ne sont pas null ou vides
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du producteur ne peut pas être vide");
        }
        
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom du producteur ne peut pas être vide");
        }
        
        // Convertir en minuscules pour comparaison
        String nomLower = nom.trim().toLowerCase();
        String prenomLower = prenom.trim().toLowerCase();
        
        // Liste de mots interdits
        String[] motsInterdits = {
            "telephone", "tele", "phone", "mobile", "cellulaire",
            "email", "mail", "courriel", "e-mail",
            "adresse", "address", "location", "lieu",
            "motdepasse", "password", "mdp", "pass",
            "admin", "administrateur", "moderateur",
            "test", "demo", "exemple", "sample"
        };
        
        // Vérifier si les noms contiennent des mots interdits
        for (String mot : motsInterdits) {
            if (nomLower.contains(mot) || prenomLower.contains(mot)) {
                throw new IllegalArgumentException(
                    "Les noms ne peuvent pas contenir des termes comme 'telephone', 'email', etc. Veuillez entrer votre vrai nom.");
            }
        }
        
        // Vérifier la longueur minimale
        if (nom.trim().length() < 2) {
            throw new IllegalArgumentException("Le nom doit contenir au moins 2 caractères");
        }
        
        if (prenom.trim().length() < 2) {
            throw new IllegalArgumentException("Le prénom doit contenir au moins 2 caractères");
        }
        
        // Vérifier qu'il ne s'agit pas de chiffres uniquement
        if (nom.trim().matches("\\d+") || prenom.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Les noms ne peuvent pas être composés uniquement de chiffres");
        }
    }
}