package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CategorieService {
    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;
    private final FileStorageService fileStorageService;

    public CategorieService(CategorieRepository categorieRepository, ProduitRepository produitRepository, FileStorageService fileStorageService) {
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
        this.fileStorageService = fileStorageService;
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
            throw new SecurityException("Accès refusé : seul un ADMIN peut effectuer cette action !");
        }
    }
    public Categorie creerCategorie(String libelle, MultipartFile photo) throws IOException {
        Categorie categorie = new Categorie();
        categorie.setLibelle(libelle);
        categorie.setDateAjout(LocalDate.now());
        
        // Handle photo upload if provided
        if (photo != null && !photo.isEmpty()) {
            try {
                System.out.println("Tentative d'upload de la photo: " + photo.getOriginalFilename());
                String fileName = fileStorageService.storeFile(photo);
                System.out.println("Fichier enregistré avec le nom: " + fileName);
                
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/suguconnect/files/download/")
                        .path(fileName)
                        .toUriString();
                System.out.println("URL de téléchargement construite: " + fileDownloadUri);
                
                categorie.setPhotoUrl(fileDownloadUri);
                System.out.println("Photo URL définie dans la catégorie: " + categorie.getPhotoUrl());
            } catch (Exception e) {
                System.err.println("Erreur lors du téléchargement de la photo : " + e.getMessage());
                e.printStackTrace();
                // Continue with category creation even if photo upload fails
            }
        } else {
            System.out.println("Aucune photo fournie ou photo vide");
        }

        System.out.println("Enregistrement de la catégorie: " + categorie.getLibelle());
        Categorie savedCategorie = categorieRepository.save(categorie);
        System.out.println("Catégorie enregistrée avec ID: " + savedCategorie.getId());
        if (savedCategorie.getPhotoUrl() != null) {
            System.out.println("Photo URL de la catégorie enregistrée: " + savedCategorie.getPhotoUrl());
        }
        
        return savedCategorie;
    }
    public Categorie modifierCategorie(int idCategorie , Categorie categorie){
        Categorie categorieaModifier = categorieRepository.findById(idCategorie)
                .orElseThrow(()->new EntityNotFoundException("Cette categorie n'existe pas"));
        categorieaModifier.setLibelle(categorie.getLibelle());
        return categorieRepository.save(categorieaModifier);
    }
    public String supprimerCategorie(int idCategorie){
        verifierRoleAdmin();
        Categorie categorie = categorieRepository.findById(idCategorie)
                .orElseThrow(()->new EntityNotFoundException("Cette categorie n'existe pas"));
        List<Produit> produitAssocies = produitRepository.findByCategorieId(idCategorie);
        if(!produitAssocies.isEmpty()){
            throw new IllegalArgumentException("On ne peut pas supprimer une catégorie contenant des produits");
        }
        categorieRepository.delete(categorie);
        return "Catégorie supprimée avec succès";
    }
    public List<Categorie> listerCategorie(){
        return categorieRepository.findAll();
    }
    public Categorie recupererUneCategorie(int categorieId){
       return categorieRepository.findById(categorieId)
                .orElseThrow(()->new EntityNotFoundException("La catégorie n'existe pas"));
    }
    public List<Produit> listProduitParCategorie(int categorieId){
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(()->new EntityNotFoundException("La catégorie n'existe pas "));
        return produitRepository.findByCategorieId(categorieId);
    }
}