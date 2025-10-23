package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.Consommateur;
import odk.SuguConnect.Entity.Panier;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Mapper.ConsommateurMapper;
import odk.SuguConnect.Repository.ConsommateurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsable UNIQUEMENT de la gestion des consommateurs (CRUD)
 * Respecte le principe SRP - Single Responsibility Principle
 */
@Service
@RequiredArgsConstructor
public class ConsommateurService {
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscrire un nouveau consommateur
     * Responsabilité: Création de compte consommateur uniquement
     */
    public String inscriptionConsommateur(ConsommateurRequestDTO dto, String telephone) {
        verifierCompteNonExistant(telephone);
        
        Consommateur consommateur = creerConsommateur(dto);
        Panier panier = creerPanierPourConsommateur(consommateur);
        consommateur.setPanier(panier);
        
        consommateurRepository.save(consommateur);
        return "Soyez le bienvenue ";
    }
    
    /**
     * Récupérer tous les consommateurs
     * Responsabilité: Lecture de données consommateurs
     */
    public List<ConsommateurResponseDTO> recupererLesConsommateurs() {
        return consommateurRepository.findAll().stream()
                .map(ConsommateurMapper::toResponse)
                .toList();
    }
    
    /**
     * Récupérer un consommateur par ID
     * Responsabilité: Lecture d'un consommateur spécifique
     */
    public ConsommateurResponseDTO recupererUnConsommateur(int id) {
        Consommateur consommateur = findConsommateurById(id);
        return ConsommateurMapper::toResponse(consommateur);
    }
    
    /**
     * Modifier les informations d'un consommateur
     * Responsabilité: Mise à jour des données consommateur
     */
    public String modifierInformationConsommateur(ConsommateurRequestDTO dto, int id) {
        Consommateur consommateur = findConsommateurById(id);
        mettreAJourInformations(consommateur, dto);
        consommateurRepository.save(consommateur);
        return "Vos informations ont été modifiées avec succès";
    }
    
    /**
     * Supprimer un consommateur
     * Responsabilité: Suppression de compte consommateur
     */
    public String supprimerConsommateur(int id) {
        Consommateur consommateur = findConsommateurById(id);
        consommateurRepository.delete(consommateur);
        return "Le compte a été supprimé avec succès";
    }
    
    /**
     * Voir tous les produits disponibles
     * Responsabilité: Consultation publique des produits
     * Note: Méthode de lecture simple, pas de logique métier complexe
     */
    public List<Produit> voirTousLesProduitsDisponibles() {
        return produitRepository.findAllByStockDisponibleGreaterThan(0);
    }
    
    // ========== Méthodes privées utilitaires ==========
    
    private void verifierCompteNonExistant(String telephone) {
        if (consommateurRepository.findByTelephone(telephone) != null) {
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
    }
    
    private Consommateur creerConsommateur(ConsommateurRequestDTO dto) {
        Consommateur consommateur = ConsommateurMapper.toEntity(dto, new Consommateur());
        consommateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        consommateur.setRole(Role.CONSOMMATEUR);
        consommateur.setDateInscription(LocalDate.now());
        consommateur.setActif(true);
        return consommateur;
    }
    
    private Panier creerPanierPourConsommateur(Consommateur consommateur) {
        Panier panier = new Panier();
        panier.setConsommateur(consommateur);
        return panier;
    }
    
    private Consommateur findConsommateurById(int id) {
        return consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
    }
    
    private void mettreAJourInformations(Consommateur consommateur, ConsommateurRequestDTO dto) {
        consommateur.setNom(dto.nom());
        consommateur.setPrenom(dto.prenom());
        consommateur.setTelephone(dto.telephone());
        consommateur.setEmail(dto.email());
        consommateur.setLocalisation(dto.localisation());
        
        // Encoder le mot de passe uniquement s'il est fourni
        if (dto.motDePasse() != null && !dto.motDePasse().isEmpty()) {
            consommateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        }
    }
}