package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategorieServiceIntegrationTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreerCategorieWithPhotoIntegration() throws IOException {
        // Arrange
        String libelle = "Légumes";
        String originalFileName = "carottes.jpg";
        String storedFileName = "uuid-carottes.jpg";
        String expectedPhotoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/suguconnect/files/download/")
                .path(storedFileName)
                .toUriString();
        
        // Create a real mock multipart file
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                originalFileName,
                "image/jpeg",
                "fake image content".getBytes(StandardCharsets.UTF_8)
        );
        
        // Mock the file storage service
        when(fileStorageService.storeFile(photo)).thenReturn(storedFileName);
        
        // Mock the category repository
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(1);
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, photo);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        // Verify that the photo URL is correctly set
        assertEquals(expectedPhotoUrl, result.getPhotoUrl());
        
        // Verify that the file storage service was called
        verify(fileStorageService, times(1)).storeFile(photo);
        
        // Verify that the category repository was called
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
    
    @Test
    public void testCreerCategorieWithoutPhotoIntegration() throws IOException {
        // Arrange
        String libelle = "Fruits";
        
        // Mock the category repository
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(2);
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        assertNull(result.getPhotoUrl());
        
        // Verify that the file storage service was NOT called
        verify(fileStorageService, never()).storeFile(any());
        
        // Verify that the category repository was called
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
}