package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreerCategorieWithPhoto() throws IOException {
        // Arrange
        String libelle = "Fruits et légumes";
        MultipartFile photo = mock(MultipartFile.class);
        String fileName = "test-photo.jpg";
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/suguconnect/files/download/")
                .path(fileName)
                .toUriString();
        
        when(photo.isEmpty()).thenReturn(false);
        when(fileStorageService.storeFile(photo)).thenReturn(fileName);
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(1);
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, photo);
        
        // Assert
        assertNotNull(result);
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        assertEquals(fileDownloadUri, result.getPhotoUrl());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
    
    @Test
    public void testCreerCategorieWithoutPhoto() throws IOException {
        // Arrange
        String libelle = "Fruits et légumes";
        
        when(categorieRepository.save(any(Categorie.class))).thenAnswer(invocation -> {
            Categorie categorie = invocation.getArgument(0);
            categorie.setId(1);
            return categorie;
        });
        
        // Act
        Categorie result = categorieService.creerCategorie(libelle, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(libelle, result.getLibelle());
        assertNotNull(result.getDateAjout());
        assertNull(result.getPhotoUrl());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
}