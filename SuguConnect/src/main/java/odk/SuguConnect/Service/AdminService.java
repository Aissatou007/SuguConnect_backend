package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Mapper.AdminMapper;
import odk.SuguConnect.Repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ProducteurRepository producteurRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConsommateurRepository consommateurRepository;
    private final ProduitRepository produitRepository;


    public AdminService(AdminRepository adminRepository, ProducteurRepository producteurRepository, CommandeRepository commandeRepository, PaiementRepository paiementRepository, PasswordEncoder passwordEncoder, ConsommateurRepository consommateurRepository, ProduitRepository produitRepository) {
        this.adminRepository = adminRepository;
        this.producteurRepository = producteurRepository;
        this.commandeRepository = commandeRepository;
        this.paiementRepository = paiementRepository;
        this.passwordEncoder = passwordEncoder;
        this.consommateurRepository = consommateurRepository;
        this.produitRepository = produitRepository;
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
            throw new IllegalArgumentException("Cet admin (email/telephone) existe déjà");
        }
        Admin admin = AdminMapper.toEntity(dto, new Admin());
        admin.setMotDePasse(passwordEncoder.encode(dto.motDePasse()));
        admin.setRole(Role.ADMIN);
        admin.setDateInscription(LocalDate.now());
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
    public Producteur createProducteur(Producteur producteur) {
        verifierRoleAdmin();
        if (producteurRepository.findByTelephone(producteur.getTelephone()) != null) {
            throw new IllegalArgumentException("Ce producteur existe déjà");
        }
        producteur.setRole(Role.PRODUCTEUR);
        producteur.setStatutProducteur(StatutProducteur.EN_ATTENTE);
        producteur.setDateInscription(LocalDate.now());
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



}











