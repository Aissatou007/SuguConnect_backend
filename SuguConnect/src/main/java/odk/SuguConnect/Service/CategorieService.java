package odk.SuguConnect.Service;

import jakarta.persistence.EntityNotFoundException;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class CategorieService {
    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public CategorieService(CategorieRepository categorieRepository, ProduitService produitService, ProduitRepository produitRepository) {
        this.categorieRepository = categorieRepository;
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
            throw new SecurityException("Accès refusé : seul un ADMIN peut effectuer cette action !");
        }
    }
    public Categorie creerCategorie(String libelle, MultipartFile photo) throws IOException {
        Categorie categorie = new Categorie();
        categorie.setLibelle(libelle);

        return categorieRepository.save(categorie);
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
            throw new IllegalArgumentException("On ne peux pas supprimer une catégorie contenant des produits");
        }
        categorieRepository.delete(categorie);
        return "Categorie supprimé avec succès";
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
