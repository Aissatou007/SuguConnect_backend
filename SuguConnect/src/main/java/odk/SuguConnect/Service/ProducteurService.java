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

/**
 * Service responsable UNIQUEMENT de la gestion des producteurs (CRUD)
 * Respecte le principe SRP - Single Responsibility Principle
 */
@Service
@RequiredArgsConstructor
public class ProducteurService {
    private final ProducteurRepository producteurRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscrire un nouveau producteur
     * Responsabilité: Création de compte producteur uniquement
     */
    public String inscriptionProducteur(ProducteurRequestDTO dto, String telephone) {
        verifierCompteNonExistant(telephone);
        
        Producteur producteur = creerProducteur(dto);
        producteurRepository.save(producteur);
        
        return "Soyez le bienvenue ";
    }
    
    /**
     * Récupérer tous les producteurs
     * Responsabilité: Lecture de données producteurs
     */
    public List<ProducteurResponseDTO> recupererLesProducteurs() {
        return producteurRepository.findAll().stream()
                .map(ProducteurMapper::toResponse)
                .toList();
    }
    
    /**
     * Récupérer un producteur par ID
     * Responsabilité: Lecture d'un producteur spécifique
     */
    public ProducteurResponseDTO recupererUnProducteur(int id) {
        Producteur producteur = findProducteurById(id);
        return ProducteurMapper.toResponse(producteur);
    }
    
    /**
     * Modifier les informations d'un producteur
     * Responsabilité: Mise à jour des données producteur
     */
    public String modifierInformationProducteur(ProducteurRequestDTO dto, int id) {
        Producteur producteur = findProducteurById(id);
        mettreAJourInformations(producteur, dto);
        producteurRepository.save(producteur);
        return "Vos informations ont été modifiées avec succès";
    }
    
    /**
     * Supprimer un producteur
     * Responsabilité: Suppression de compte producteur
     */
    public String supprimerProducteur(int id) {
        Producteur producteur = findProducteurById(id);
        producteurRepository.delete(producteur);
        return "Le compte a été supprimé avec succès";
    }
    
    // ========== Méthodes privées utilitaires ==========
    
    private void verifierCompteNonExistant(String telephone) {
        if (producteurRepository.findByTelephone(telephone) != null) {
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
    }
    
    private Producteur creerProducteur(ProducteurRequestDTO dto) {
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
        producteur.setNom(dto.nom());
        producteur.setPrenom(dto.prenom());
        producteur.setTelephone(dto.telephone());
        producteur.setEmail(dto.email());
        producteur.setLocalisation(dto.localisation());
        producteur.setDescription(dto.description());
        
        // Encoder le mot de passe uniquement s'il est fourni
        if (dto.motDePasse() != null && !dto.motDePasse().isEmpty()) {
            producteur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        }
    }
}