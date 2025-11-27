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


@Service
@RequiredArgsConstructor
public class ConsommateurService {
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;
    private final PasswordEncoder passwordEncoder;

    public String inscriptionConsommateur(ConsommateurRequestDTO dto, String telephone) {
        verifierCompteNonExistant(telephone);
        
        Consommateur consommateur = creerConsommateur(dto);
        Panier panier = creerPanierPourConsommateur(consommateur);
        consommateur.setPanier(panier);
        
        consommateurRepository.save(consommateur);
        return "Soyez le bienvenue ";
    }

    public List<ConsommateurResponseDTO> recupererLesConsommateurs() {
        return consommateurRepository.findAll().stream()
                .map(ConsommateurMapper::toResponse)
                .toList();
    }
    

    public ConsommateurResponseDTO recupererUnConsommateur(int id) {
        Consommateur consommateur = findConsommateurById(id);
        return ConsommateurMapper.toResponse(consommateur);
    }
    

    public String modifierInformationConsommateur(ConsommateurRequestDTO dto, int id) {
        Consommateur consommateur = findConsommateurById(id);
        mettreAJourInformations(consommateur, dto);
        consommateurRepository.save(consommateur);
        return "Vos informations ont été modifiées avec succès";
    }
    

    public String supprimerConsommateur(int id) {
        Consommateur consommateur = findConsommateurById(id);
        consommateurRepository.delete(consommateur);
        return "Le compte a été supprimé avec succès";
    }
    

    public List<Produit> voirTousLesProduitsDisponibles() {
        return produitRepository.findAllByStockDisponibleGreaterThan(0);
    }

    public List<Produit> filtrerProduitsDisponiblesParNom(String nom) {
        List<Produit> produits = produitRepository.findByNomContainingIgnoreCase(nom);
        return produits.stream()
                .filter(produit -> produit.getStockDisponible() > 0)
                .toList();
    }

    public List<Produit> voirProduitsDisponiblesParCategorie(int categorieId) {
        List<Produit> produits = produitRepository.findByCategorieId(categorieId);
        return produits.stream()
                .filter(produit -> produit.getStockDisponible() > 0)
                .toList();
    }

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

        if (dto.motDePasse() != null && !dto.motDePasse().isEmpty()) {
            consommateur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        }
    }

    // ========== Gestion des favoris ==========
    
    @org.springframework.transaction.annotation.Transactional
    public String ajouterProduitAuxFavoris(int consommateurId, int produitId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));

        // Initialiser la liste si elle est null
        if (consommateur.getProduit() == null) {
            consommateur.setProduit(new java.util.ArrayList<>());
        }

        // Vérifier si le produit est déjà dans les favoris
        if (consommateur.getProduit().contains(produit)) {
            return "Ce produit est déjà dans vos favoris";
        }

        consommateur.getProduit().add(produit);
        consommateurRepository.save(consommateur);
        return String.format("Produit %s ajouté aux favoris avec succès", produit.getNom());
    }

    @org.springframework.transaction.annotation.Transactional
    public String retirerProduitDesFavoris(int consommateurId, int produitId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));

        if (consommateur.getProduit() == null || !consommateur.getProduit().contains(produit)) {
            return "Ce produit n'est pas dans vos favoris";
        }

        consommateur.getProduit().remove(produit);
        consommateurRepository.save(consommateur);
        return String.format("Produit %s retiré des favoris avec succès", produit.getNom());
    }

    public List<Produit> voirFavoris(int consommateurId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        if (consommateur.getProduit() == null) {
            return new java.util.ArrayList<>();
        }
        return consommateur.getProduit();
    }

    public List<Produit> rechercherFavorisParCategorie(int consommateurId, int categorieId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        if (consommateur.getProduit() == null) {
            return new java.util.ArrayList<>();
        }
        
        // Filtrer les favoris par catégorie
        return consommateur.getProduit().stream()
                .filter(produit -> produit.getCategorie() != null && 
                                  produit.getCategorie().getId() == categorieId)
                .toList();
    }

    /**
     * Recommande des produits similaires basés sur les favoris du consommateur
     * 
     * Règles :
     * 1. Prioriser les produits des mêmes catégories que les favoris
     * 2. Uniquement les produits en stock (stockDisponible > 0)
     * 3. Exclure les produits déjà en favoris
     * 4. Trier par ID décroissant (nouveaux produits d'abord)
     */
    public List<Produit> recommanderProduits(int consommateurId) {
        Consommateur consommateur = findConsommateurById(consommateurId);
        
        // Si le consommateur n'a pas de favoris, retourner une liste vide
        if (consommateur.getProduit() == null || consommateur.getProduit().isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 1. Extraire les IDs des catégories des produits favoris
        List<Integer> categoriesIds = consommateur.getProduit().stream()
                .filter(produit -> produit.getCategorie() != null)
                .map(produit -> produit.getCategorie().getId())
                .distinct()
                .toList();

        if (categoriesIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 2. Récupérer tous les produits en stock des catégories des favoris
        List<Produit> produitsRecommandes = new java.util.ArrayList<>();
        for (Integer categorieId : categoriesIds) {
            List<Produit> produitsCategorie = produitRepository.findByCategorieId(categorieId);
            produitsRecommandes.addAll(produitsCategorie);
        }

        // 3. Filtrer : uniquement produits en stock
        produitsRecommandes = produitsRecommandes.stream()
                .filter(produit -> produit.getStockDisponible() > 0)
                .toList();

        // 4. Exclure les produits déjà en favoris
        List<Integer> favorisIds = consommateur.getProduit().stream()
                .map(Produit::getId)
                .toList();
        
        produitsRecommandes = produitsRecommandes.stream()
                .filter(produit -> !favorisIds.contains(produit.getId()))
                .toList();

        // 5. Trier par ID décroissant (nouveaux produits d'abord)
        // Les produits avec un ID plus élevé sont généralement plus récents
        produitsRecommandes = produitsRecommandes.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId()))
                .toList();

        // Supprimer les doublons (au cas où un produit serait dans plusieurs catégories)
        return produitsRecommandes.stream()
                .distinct()
                .toList();
    }
}