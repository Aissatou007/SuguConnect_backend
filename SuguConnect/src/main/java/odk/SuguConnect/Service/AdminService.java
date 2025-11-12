package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutCommande;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Mapper.AdminMapper;
import odk.SuguConnect.Mapper.LivreurMapper;
import odk.SuguConnect.Mapper.ProducteurMapper;
import odk.SuguConnect.Repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;
    private final LivreurRepository livreurRepository;


    public AdminService(AdminRepository adminRepository, ProducteurRepository producteurRepository, CommandeRepository commandeRepository, PaiementRepository paiementRepository, PasswordEncoder passwordEncoder, ConsommateurRepository consommateurRepository, ProduitRepository produitRepository, LivreurRepository livreurRepository) {
        this.adminRepository = adminRepository;
        this.producteurRepository = producteurRepository;
        this.commandeRepository = commandeRepository;
        this.paiementRepository = paiementRepository;
        this.passwordEncoder = passwordEncoder;
        this.consommateurRepository = consommateurRepository;
        this.produitRepository = produitRepository;
        this.livreurRepository = livreurRepository;
    }

    private void verifierRoleAdmin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new SecurityException("Aucun utilisateur connecté !");
        }

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new SecurityException("Accès refusé : rôle ADMIN requis !");
        }
    }
    public Admin creationAdminParDefaut(String nom, String prenom, String email , String telephone, String motDePasse){
        if(adminRepository.findByTelephone(telephone) != null || adminRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Cet admin existe déjà !");
        }
        Admin admin = new Admin();
        admin.setNom(nom);
        admin.setPrenom(prenom);
        admin.setEmail(email);
        admin.setTelephone(telephone);
        admin.setMotDePasse(passwordEncoder.encode(motDePasse));
        admin.setRole(Role.ADMIN);
        admin.setDateInscription(LocalDate.now());
        return adminRepository.save(admin);

    }
    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        verifierRoleAdmin();
        if (adminRepository.existsByEmail(dto.email()) || adminRepository.findByTelephone(dto.telephone()) != null) {
            throw new IllegalArgumentException("Cet admin  existe déjà");
        }
        Admin admin = AdminMapper.toEntity(dto, new Admin());
        admin.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        admin.setRole(Role.ADMIN);
        admin.setDateInscription(LocalDate.now());
        admin.setActif(true);
        Admin saved = adminRepository.save(admin);
        return AdminMapper.toResponse(saved);
    }
    public List<AdminResponseDTO> recupererLesAdmins(){
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(AdminMapper::toResponse).toList();
    }
    public AdminResponseDTO recupererUnAdmin(int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));
        return AdminMapper.toResponse(admin);
    }
    public String modifierInformationAdmin(AdminRequestDTO adminRequestDTO, int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));

        admin.setNom(adminRequestDTO.nom());
        admin.setPrenom(adminRequestDTO.prenom());
        admin.setTelephone(adminRequestDTO.telephone());
        admin.setEmail(adminRequestDTO.email());
        admin.setLocalisation(adminRequestDTO.localisation());
        admin.setMotDePasse(adminRequestDTO.motDePasse());

        adminRepository.save(admin);

        return "Les informations de l'admin ont été modifiées avec succès";
    }
    public String supprimerAdmin(int id){
        Admin admin = adminRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Cet admin n'existe pas"));

        adminRepository.delete(admin);

        return "Le compte admin a été supprimé avec succès";
    }
    
    //Activer ou désactiver un compte admin

    public String toggleAdminStatus(int id, boolean actif) {
        verifierRoleAdmin();
        Admin admin = adminRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Cet admin n'existe pas"));
        
        admin.setActif(actif);
        adminRepository.save(admin);
        
        return actif ? "Le compte admin a été activé avec succès" : "Le compte admin a été désactivé avec succès";
    }
    public Producteur createProducteur(ProducteurRequestDTO dto) {
        verifierRoleAdmin();
        
        if (producteurRepository.findByTelephone(dto.telephone()) != null) {
            throw new IllegalArgumentException("Ce producteur existe déjà");
        }
        
        Producteur producteur = ProducteurMapper.toEntity(dto, new Producteur());
        producteur.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        producteur.setRole(Role.PRODUCTEUR);
        producteur.setStatutProducteur(StatutProducteur.ACCEPTE);
        producteur.setDateInscription(LocalDate.now());
        producteur.setActif(true);
        
        return producteurRepository.save(producteur);
    }


    public Producteur changeProducteurStatut(int producteurId, StatutProducteur nouveauStatut, String raisonRejet) {
        verifierRoleAdmin();
        Producteur p = producteurRepository.findById(producteurId)
                .orElseThrow(() -> new EntityNotFoundException("Producteur introuvable"));
        p.setStatutProducteur(nouveauStatut);
        if (nouveauStatut == StatutProducteur.REFUSE) {
            p.setMotifDeRejet(raisonRejet);
        } else if (nouveauStatut == StatutProducteur.ACCEPTE) {

        }
        return producteurRepository.save(p);
    }
    public List<Consommateur> recupererLesConsommateurs() {
        verifierRoleAdmin();
        return consommateurRepository.findAll();
    }
    public List<Producteur> recupererLesProducteurs() {
        verifierRoleAdmin();
        return producteurRepository.findAll();
    }
    public List<Produit> recupererTousLesProduits() {
       verifierRoleAdmin();
        return produitRepository.findAll();
    }
    public List<Commande> recupererToutesLesCommandes() {
       verifierRoleAdmin();
        return commandeRepository.findAll();
    }

    public List<Paiement> recupererTousLesPaiements() {
       verifierRoleAdmin();
        return paiementRepository.findAll();
    }
    
    public List<Livreur> recupererTousLesLivreurs() {
        verifierRoleAdmin();
        return livreurRepository.findAll();
    }
    
    public List<LivreurResponseDTO> recupererLivreursDisponibles() {
        verifierRoleAdmin();
        return livreurRepository.findByDisponibleTrue().stream()
                .map(LivreurMapper::toResponse)
                .toList();
    }
    public Commande recupererCommandeParId(int id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));
    }

    public Commande modifierStatutCommande(int id, String statut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée avec l'ID: " + id));

        try {
            StatutCommande nouveauStatut = StatutCommande.valueOf(statut);
            commande.setStatutCommande(nouveauStatut);
            return commandeRepository.save(commande);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide: " + statut);
        }
    }

    public Commande assignerLivreur(int commandeId, int livreurId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));

        commande.setLivreurPrefere(livreur);
        return commandeRepository.save(commande);
    }
    public Produit recupererProduitParId(int id) {
        verifierRoleAdmin();
        return produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
    }
    public List<Commande> recupererHistoriqueVentes() {
        verifierRoleAdmin(); // vérifie que l'utilisateur est admin
        // Récupère toutes les commandes avec leurs produits et consommateur
        List<Commande> commandes = commandeRepository.findAll();
        // Tu peux calculer le montant total si nécessaire
        commandes.forEach(commande -> {
            double total = commande.getCommandeProduits().stream()
                    .mapToDouble(cp -> cp.getPrixUnitaire() * cp.getQuantite())
                    .sum();
            commande.setMontantTotal(total);
        });
        return commandes;
    }
    public List<Commande> recupererHistoriqueVentesAvecDetails() {
        verifierRoleAdmin();
        return commandeRepository.findAll();
        // Assure-toi que l'entité Commande contient les relations @ManyToOne vers Produit et Consommateur
    }
    public List<CommandeProduit> getCommandesPourProduit(int produitId) {
        // récupère toutes les commandes
        List<Commande> toutesCommandes = commandeRepository.findAll();

        // filtre toutes les commandes pour ne garder que celles qui contiennent le produit
        List<CommandeProduit> commandesProduit = toutesCommandes.stream()
                .flatMap(c -> c.getCommandeProduits().stream())
                .filter(cp -> cp.getProduit().getId() == produitId)
                .collect(Collectors.toList());

        return commandesProduit;
    }

}