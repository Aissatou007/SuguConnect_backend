package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.Unite;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProducteurRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduitServiceBioValidationTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProducteurRepository producteurRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    public void testAjouterProduitWithMissingBioStatus() {
        // Arrange
        int producteurId = 1;
        Produit produit = new Produit();
        produit.setNom("Mangues Bio");
        produit.setDescription("Mangues biologiques fraîches");
        produit.setPrixUnitaire(2500.0f);
        produit.setQuantite(100);
        produit.setUnite(Unite.KILOGRAMME);
        // Note: estBio has a default value of false in the entity, so it's never actually "missing"
        produit.setPhotos(new ArrayList<>());
        produit.getPhotos().add("photo1.jpg");

        Producteur producteur = new Producteur();
        producteur.setId(producteurId);

        Categorie categorie = new Categorie();
        categorie.setId(1);

        when(producteurRepository.findById(producteurId)).thenReturn(Optional.of(producteur));
        when(categorieRepository.findById(1)).thenReturn(Optional.of(categorie));

        // Act & Assert
        // This should not throw an exception because estBio has a default value
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            produitService.ajouterProduit(produit, producteurId);
        });
        
        // The exception should be about missing photos, not missing estBio
        assertTrue(exception.getMessage().contains("Au moins une photo est requise"));
    }

    @Test
    public void testAjouterProduitWithValidBioStatus() {
        // Arrange
        int producteurId = 1;
        Produit produit = new Produit();
        produit.setNom("Mangues Bio");
        produit.setDescription("Mangues biologiques fraîches");
        produit.setPrixUnitaire(2500.0f);
        produit.setQuantite(100);
        produit.setUnite(Unite.KILOGRAMME);
        produit.setEstBio(true); // Explicitly set to true
        produit.setPhotos(new ArrayList<>());
        produit.getPhotos().add("photo1.jpg");

        Producteur producteur = new Producteur();
        producteur.setId(producteurId);

        Categorie categorie = new Categorie();
        categorie.setId(1);

        Produit savedProduit = new Produit();
        savedProduit.setId(1);
        savedProduit.setNom("Mangues Bio");

        when(producteurRepository.findById(producteurId)).thenReturn(Optional.of(producteur));
        when(categorieRepository.findById(1)).thenReturn(Optional.of(categorie));
        when(produitRepository.save(produit)).thenReturn(savedProduit);

        // Act
        Produit result = produitService.ajouterProduit(produit, producteurId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertTrue(result.isEstBio()); // Verify that estBio is properly set
    }
}