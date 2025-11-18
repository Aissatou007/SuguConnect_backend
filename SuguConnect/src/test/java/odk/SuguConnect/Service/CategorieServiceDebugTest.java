package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategorieServiceDebugTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreerCategorieWithRealPhoto() throws IOException {
        // Arrange
        String libelle = "Fruits";
        // Create a real mock multipart file instead of a mock
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes(StandardCharsets.UTF_8)
        );
        
        String fileName = "generated-uuid.jpg";
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/suguconnect/files/download/")
                .path(fileName)
                .toUriString();
        
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
        assertEquals(fileDownloadUri, result.getPhotoUrl());
        verify(fileStorageService, times(1)).storeFile(photo);
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
    
    @Test
    public void testCreerCategorieWithEmptyPhoto() throws IOException {
        // Arrange
        String libelle = "Fruits";
        MultipartFile photo = mock(MultipartFile.class);
        
        when(photo.isEmpty()).thenReturn(true);
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
        assertNull(result.getPhotoUrl());
        verify(fileStorageService, never()).storeFile(photo);
        verify(categorieRepository, times(1)).save(any(Categorie.class));
    }
}