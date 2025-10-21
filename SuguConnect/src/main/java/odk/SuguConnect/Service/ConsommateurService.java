package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.ConsommateurRequestDTO;
import odk.SuguConnect.DTO.Responses.ConsommateurResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutPaiement;
import odk.SuguConnect.Mapper.ConsommateurMapper;
import odk.SuguConnect.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsommateurService {
    private final ConsommateurRepository  consommateurRepository;
    private final ProduitRepository produitRepository;
    private final PanierRepository panierRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final CommandeService commandeService;

    public ConsommateurService(ConsommateurRepository consommateurRepository
            , ProduitRepository produitRepository
            , PanierRepository panierRepository
            , CommandeRepository commandeRepository
            , PaiementRepository paiementRepository, CommandeService commandeService) {
        this.consommateurRepository = consommateurRepository;
        this.produitRepository = produitRepository;
        this.panierRepository = panierRepository;
        this.commandeRepository = commandeRepository;
        this.paiementRepository = paiementRepository;
        this.commandeService = commandeService;
    }


    //Inscription d'un consommateur
    public String inscriptionConsommateur(ConsommateurRequestDTO consommateurRequestDTO, String telephone){
        Consommateur conso =  consommateurRepository.findByTelephone(telephone);
        if (conso != null){
            throw new IllegalArgumentException("Ce compte existe déjà");
        }
        Consommateur consommateur = ConsommateurMapper.toEntity(consommateurRequestDTO,new Consommateur());
        consommateur.setRole(Role.CONSOMMATEUR);
        consommateur.setDateInscription(LocalDate.now());
        Panier panier = new Panier();
        panier.setConsommateur(consommateur);
        consommateur.setPanier(panier);
        consommateurRepository.save(consommateur);
        return "Soyez le bienvenue ";
    }
    public List<ConsommateurResponseDTO> recupererLesConsommateurs(){
        List<Consommateur> consommateurs = consommateurRepository.findAll();
        return consommateurs.stream().map(ConsommateurMapper::toResponse).toList();
    }
    public ConsommateurResponseDTO recupererUnConsommateur(int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        return ConsommateurMapper.toResponse(consommateur);

    }
    public String modifierInformationConsommateur(ConsommateurRequestDTO consommateurRequestDTO,int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateur.setNom(consommateurRequestDTO.nom());
        consommateur.setPrenom(consommateurRequestDTO.prenom());
        consommateur.setTelephone(consommateurRequestDTO.telephone());
        consommateur.setEmail(consommateurRequestDTO.email());
        consommateur.setLocalisation(consommateurRequestDTO.localisation());
        consommateur.setMotDePasse(consommateurRequestDTO.motDePasse());
        consommateurRepository.save(consommateur);
        return "Vos informations ont été modifier avec succès";
    }
    public String supprimerConsommateur(int id){
        Consommateur consommateur = consommateurRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Ce consommateur n'a pas de compte"));
        consommateurRepository.delete(consommateur);
        return "Le compte a été supprimer avec succès";
    }
    public List<Produit> voirTousLesProduitsDisponibles(){
        List<Produit> produitsDisponibles = produitRepository.findAllByStockDisponibleGreaterThan(0);
        if(produitsDisponibles.isEmpty()){
            throw new EntityNotFoundException("Aucun produit n'est disponible pour le moment");
        }
        return produitsDisponibles;
    }

    @Transactional
    public  String ajouterProduitAuPanier( int consommateurId , int produitId , int quantite){
            Consommateur consommateur = consommateurRepository.findById(consommateurId)
                    .orElseThrow(()->new EntityNotFoundException("consommateur introuvable"));
            Panier panier = consommateur.getPanier();
            if(panier == null){
                panier = new Panier();
                panier.setConsommateur(consommateur);
                consommateur.setPanier(panier);
            }
            Produit produit = produitRepository.findById(produitId)
                    .orElseThrow(()->new EntityNotFoundException("Produit introuvable"));
            if(produit.getStockDisponible()< quantite){
                throw new IllegalArgumentException("Stock insuffisant pour ce produit ");
            }
            panier.getProduits().add(produit);
            produit.setStockDisponible(produit.getStockDisponible() - quantite);
            panierRepository.save(panier);
            return "Produit"+produit.getNom()+" ajouter à votre panier";
    }
    @Transactional
    public String retirerProduitDuPanier(int consommateurId , int produitId){
        Consommateur consommateur = consommateurRepository.findById(consommateurId)
                .orElseThrow(()-> new EntityNotFoundException("onsommateur introuvable"));
        Panier panier = consommateur.getPanier();
        if(panier == null || panier.getProduits().isEmpty()){
            throw new IllegalArgumentException("votre panier est vide");
        }
        panier.getProduits().removeIf(p -> p.getId() == produitId);
        panierRepository.save(panier);
        return "Le produit retiré du panier";
    }
    @Transactional
    public Commande passerCommande(int idConsommateur , ModePaiement modePaiement){
        return commandeService.passerCommande(idConsommateur, modePaiement);
    }
}
