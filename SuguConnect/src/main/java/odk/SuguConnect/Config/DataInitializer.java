package odk.SuguConnect.Config;

import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Enums.Unite;
import odk.SuguConnect.Repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    @Value("${admin.nom:Super}")
    private String adminNom;

    @Value("${admin.prenom:Admin}")
    private String adminPrenom;

    @Value("${admin.email:admin@suguconnect.com}")
    private String adminEmail;

    @Value("${admin.telephone:70000000}")
    private String adminTelephone;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProducteurRepository producteurRepository;
    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public DataInitializer(AdminRepository adminRepository, 
                          PasswordEncoder passwordEncoder,
                          ProducteurRepository producteurRepository,
                          CategorieRepository categorieRepository,
                          ProduitRepository produitRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.producteurRepository = producteurRepository;
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
    }

    @Bean
    @Transactional
    public CommandLineRunner initData() {
        return args -> {
            // 1. Créer l'admin par défaut
            initAdmin();
            
            // 2. Initialiser les données de test
            initCategories();
            initProducteurs();
            initProduits();
            
            System.out.println("✅ Initialisation des données de test terminée!");
        };
    }
    
    private void initAdmin() {
        // Vérifie si un admin existe déjà
        if (adminRepository.findByTelephone(adminTelephone) == null) {
            Admin admin = new Admin();
            admin.setNom(adminNom);
            admin.setPrenom(adminPrenom);
            admin.setEmail(adminEmail);
            admin.setTelephone(adminTelephone);
            admin.setMotDePasse(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setDateInscription(LocalDate.now());
            admin.setActif(true);
            admin.setLatitude(0L);
            admin.setLongitude(0L);

            adminRepository.save(admin);
            System.out.println("👤 Admin par défaut créé avec succès!");
        }
    }
    
    /**
     * Initialise les catégories de test
     */
    private void initCategories() {
        if (categorieRepository.count() == 0) {
            String[] categories = {"Fruits", "Légumes", "Céréales", "Produits Laitiers", "Viandes"};
            
            for (String catName : categories) {
                Categorie categorie = new Categorie();
                categorie.setLibelle(catName);
                categorie.setPhotoUrl("default.jpg"); // Photo par défaut
                categorie.setDateAjout(LocalDate.now());
                categorieRepository.save(categorie);
            }
            
            System.out.println("📂 " + categories.length + " catégories créées!");
        }
    }
    
    /**
     * Initialise les producteurs de test
     */
    private void initProducteurs() {
        if (producteurRepository.count() == 0) {
            // Producteur 1
            Producteur prod1 = new Producteur();
            prod1.setNom("Traoré");
            prod1.setPrenom("Mamadou");
            prod1.setTelephone("76543210");
            prod1.setEmail("mamadou@producteur.com");
            prod1.setMotDePasse(passwordEncoder.encode("producteur123"));
            prod1.setLocalisation("Bamako");
            prod1.setLatitude(0L);
            prod1.setLongitude(0L);
            prod1.setRole(Role.PRODUCTEUR);
            prod1.setStatutProducteur(StatutProducteur.ACCEPTE);
            prod1.setDateInscription(LocalDate.now());
            prod1.setActif(true);
            prod1.setDescription("Producteur de fruits biologiques");
            prod1.setNomFerme("Ferme Bio de Mamadou");
            producteurRepository.save(prod1);
            
            // Producteur 2
            Producteur prod2 = new Producteur();
            prod2.setNom("Coulibaly");
            prod2.setPrenom("Fatou");
            prod2.setTelephone("77654321");
            prod2.setEmail("fatou@producteur.com");
            prod2.setMotDePasse(passwordEncoder.encode("producteur123"));
            prod2.setLocalisation("Sikasso");
            prod2.setLatitude(0L);
            prod2.setLongitude(0L);
            prod2.setRole(Role.PRODUCTEUR);
            prod2.setStatutProducteur(StatutProducteur.ACCEPTE);
            prod2.setDateInscription(LocalDate.now());
            prod2.setActif(true);
            prod2.setDescription("Productrice de légumes frais");
            prod2.setNomFerme("Ferme de Fatou");
            producteurRepository.save(prod2);
            
            System.out.println("👥 2 producteurs créés et validés!");
        }
    }
    
    /**
     * Initialise les produits de test
     */
    private void initProduits() {
        if (produitRepository.count() == 0) {
            Producteur producteur1 = producteurRepository.findByTelephone("76543210");
            Producteur producteur2 = producteurRepository.findByTelephone("77654321");
            
            Categorie catFruits = categorieRepository.findByLibelle("Fruits").orElse(null);
            Categorie catLegumes = categorieRepository.findByLibelle("Légumes").orElse(null);
            
            if (producteur1 != null && catFruits != null) {
                // Produits du producteur 1 (Fruits)
                Produit mangues = new Produit();
                mangues.setNom("Mangues Bio");
                mangues.setDescription("Mangues fraîches et biologiques");
                mangues.setPrixUnitaire(2500.0f);
                mangues.setStockDisponible(100);
                mangues.setUnite(Unite.KILOGRAMME);
                mangues.setCategorie(catFruits);
                mangues.setProducteur(producteur1);
                produitRepository.save(mangues);
                
                Produit bananes = new Produit();
                bananes.setNom("Bananes Plantain");
                bananes.setDescription("Bananes plantain de qualité");
                bananes.setPrixUnitaire(1500.0f);
                bananes.setStockDisponible(150);
                bananes.setUnite(Unite.KILOGRAMME);
                bananes.setCategorie(catFruits);
                bananes.setProducteur(producteur1);
                produitRepository.save(bananes);
                
                Produit oranges = new Produit();
                oranges.setNom("Oranges Douces");
                oranges.setDescription("Oranges juteuses et sucrées");
                oranges.setPrixUnitaire(1800.0f);
                oranges.setStockDisponible(80);
                oranges.setUnite(Unite.KILOGRAMME);
                oranges.setCategorie(catFruits);
                oranges.setProducteur(producteur1);
                produitRepository.save(oranges);
            }
            
            if (producteur2 != null && catLegumes != null) {
                // Produits du producteur 2 (Légumes)
                Produit tomates = new Produit();
                tomates.setNom("Tomates Fraîches");
                tomates.setDescription("Tomates rouges et mûres");
                tomates.setPrixUnitaire(1200.0f);
                tomates.setStockDisponible(120);
                tomates.setUnite(Unite.KILOGRAMME);
                tomates.setCategorie(catLegumes);
                tomates.setProducteur(producteur2);
                produitRepository.save(tomates);
                
                Produit oignons = new Produit();
                oignons.setNom("Oignons Locaux");
                oignons.setDescription("Oignons cultivés localement");
                oignons.setPrixUnitaire(800.0f);
                oignons.setStockDisponible(200);
                oignons.setUnite(Unite.KILOGRAMME);
                oignons.setCategorie(catLegumes);
                oignons.setProducteur(producteur2);
                produitRepository.save(oignons);
                
                Produit carottes = new Produit();
                carottes.setNom("Carottes Bio");
                carottes.setDescription("Carottes biologiques croquantes");
                carottes.setPrixUnitaire(1000.0f);
                carottes.setStockDisponible(90);
                carottes.setUnite(Unite.KILOGRAMME);
                carottes.setCategorie(catLegumes);
                carottes.setProducteur(producteur2);
                produitRepository.save(carottes);
            }
            
            System.out.println("🛒 6 produits créés avec succès!");
        }
    }

}