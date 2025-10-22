package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Mapper.ConsommateurMapper;
import odk.SuguConnect.Repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsommateurService {
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;
    private final PanierService panierService;
    private final CommandeService commandeService;
    private final PasswordEncoder passwordEncoder;

    public ConsommateurService(ConsommateurRepository consommateurRepository,
                               ProduitRepository produitRepository,
                               PanierService panierService,
                               CommandeService commandeService,
                               PasswordEncoder passwordEncoder) {
        this.consommateurRepository = consommateurRepository;
        this.produitRepository = produitRepository;
        this.panierService = panierService;
        this.commandeService = commandeService;
        this.passwordEncoder = passwordEncoder;
    }

    // Inscription d'un consommateur
    public String inscriptionConsommateur(ConsommateurRequestDTO consommateurRequestDTO, String telephone) {
        Consommateur conso = consommateurRepository.findByTelephone(telephone);
        if (conso != null) {
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
        Consommateur consommateur = ConsommateurMapper.toEntity(consommateurRequestDTO, new Consommateur());
        consommateur.setMotDePasse(passwordEncoder.encode(consommateurRequestDTO.motDePasse()));
        consommateur.setRole(Role.CONSOMMATEUR);
        consommateur.setDateInscription(LocalDate.now());
        consommateur.setActif(true);
        Panier panier = new Panier();
        panier.setConsommateur(consommateur);
        consommateur.setPanier(panier);
        consommateurRepository.save(consommateur);
        return "Soyez le bienvenue ";
    }

    public List<ConsommateurResponseDTO> recupererLesConsommateurs() {
        List<Consommateur> consommateurs = consommateurRepository.findAll();
        return consommateurs.stream().map(ConsommateurMapper::toResponse).toList();
    }

    public ConsommateurResponseDTO recupererUnConsommateur(int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        return ConsommateurMapper.toResponse(consommateur);
    }

    public String modifierInformationConsommateur(ConsommateurRequestDTO consommateurRequestDTO, int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateur.setNom(consommateurRequestDTO.nom());
        consommateur.setPrenom(consommateurRequestDTO.prenom());
        consommateur.setTelephone(consommateurRequestDTO.telephone());
        consommateur.setEmail(consommateurRequestDTO.email());
        consommateur.setLocalisation(consommateurRequestDTO.localisation());
        // Encoder le mot de passe uniquement s'il est fourni et non vide
        if (consommateurRequestDTO.motDePasse() != null && !consommateurRequestDTO.motDePasse().isEmpty()) {
            consommateur.setMotDePasse(passwordEncoder.encode(consommateurRequestDTO.motDePasse()));
        }
        consommateurRepository.save(consommateur);
        return "Vos informations ont été modifiées avec succès";
    }

    public String supprimerConsommateur(int id) {
        Consommateur consommateur = consommateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateurRepository.delete(consommateur);
        return "Le compte a été supprimé avec succès";
    }

    public List<Produit> voirTousLesProduitsDisponibles() {
        // Retourne tous les produits avec stock > 0
        // Si aucun produit n'est disponible, retourne une liste vide (meilleure pratique REST)
        return produitRepository.findAllByStockDisponibleGreaterThan(0);
    }

    // Méthodes pour gérer le panier
    @Transactional
    public String ajouterProduitAuPanier(int idConsommateur, int idProduit, int quantite) {
        return panierService.ajouterProduitAuPanier(idConsommateur, idProduit, quantite);
    }

    @Transactional
    public String retirerProduitDuPanier(int idConsommateur, int idProduit) {
        return panierService.retirerProduitDuPanier(idConsommateur, idProduit);
    }

    @Transactional
    public Commande passerCommande(int idConsommateur, ModePaiement modePaiement) {
        return commandeService.passerCommande(idConsommateur, modePaiement);
    }
}