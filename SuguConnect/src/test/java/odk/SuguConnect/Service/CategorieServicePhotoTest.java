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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategorieServicePhotoTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreerCategorieWithPhoto_Success() throws IOException {
        // Arrange
        String libelle = "Fruits";
        String fileName = "test-image.jpg";
        String storedFileName = "uuid-test-image.jpg";
        String expectedPhotoUrl = "/suguconnect/files/download/" + storedFileName;
        
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                fileName,
                "image/jpeg",
                "test image content".getBytes(StandardCharsets.UTF_8)
        );
        
        when(fileStorageService.storeFile(photo)).thenReturn(storedFileName);
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(1);
            categorie.setDateAjout(LocalDate.now());
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, photo);
        
        // Assert
        assertNotNull(result);
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        assertEquals(expectedPhotoUrl, result.getPhotoUrl());
        
        verify(fileStorageService, times(1)).storeFile(photo);
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
    
    @Test
    public void testCreerCategorieWithoutPhoto_Success() throws IOException {
        // Arrange
        String libelle = "Légumes";
        
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(2);
            categorie.setDateAjout(LocalDate.now());
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        assertNull(result.getPhotoUrl());
        
        verify(fileStorageService, never()).storeFile(any());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
}