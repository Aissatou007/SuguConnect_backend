package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Enums.StatutProducteur;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProducteurRepository producteurRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    public void testAjouterProduitAmeliore() {
        // Arrange
        int producteurId = 1;
        int categorieId = 1;
        
        Producteur producteur = new Producteur();
        producteur.setId(producteurId);
        producteur.setStatutProducteur(StatutProducteur.ACCEPTE);
        
        Categorie categorie = new Categorie();
        categorie.setId(categorieId);
        
        Produit produit = new Produit();
        produit.setNom("Carottes Bio");
        produit.setPhotos(Arrays.asList("photo1.jpg"));
        
        when(producteurRepository.findById(producteurId)).thenReturn(Optional.of(producteur));
        when(categorieRepository.findById(categorieId)).thenReturn(Optional.of(categorie));
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        
        // Act
        Produit result = produitService.ajouterProduitAmeliore(produit, producteurId, categorieId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Carottes Bio", result.getNom());
        verify(produitRepository, times(1)).save(produit);
    }
    
    @Test
    public void testGetProduitsParCategorie() {
        // Arrange
        int categorieId = 1;
        
        Categorie categorie = new Categorie();
        categorie.setId(categorieId);
        categorie.setLibelle("Légumes");
        
        Producteur producteur = new Producteur();
        producteur.setId(1);
        producteur.setNom("Dupont");
        producteur.setPrenom("Jean");
        
        Produit produit = new Produit();
        produit.setId(1);
        produit.setNom("Carottes");
        produit.setPrixUnitaire(1000.0f);
        produit.setProducteur(producteur);
        
        when(categorieRepository.findById(categorieId)).thenReturn(Optional.of(categorie));
        when(produitRepository.findByCategorieId(categorieId)).thenReturn(Arrays.asList(produit));
        
        // Act
        odk.SuguConnect.DTO.Responses.ProduitsParCategorieDTO result = produitService.getProduitsParCategorie(categorieId);
        
        // Assert
        assertNotNull(result);
        assertEquals(categorieId, result.categorieId());
        assertEquals("Légumes", result.categorieNom());
        assertEquals(1, result.produitsExistants().size());
        assertEquals("Carottes", result.produitsExistants().get(0).nom());
    }
}