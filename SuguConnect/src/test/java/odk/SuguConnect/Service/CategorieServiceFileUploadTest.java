package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Repository.CategorieRepository;
import odk.SuguConnect.Repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategorieServiceFileUploadTest {

    @Mock
    private CategorieRepository categorieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    public void testCreerCategorieWithPhotoUploadSuccess() throws IOException {
        // Arrange
        String libelle = "Fruits";
        MultipartFile photo = mock(MultipartFile.class);
        String fileName = "test-photo.jpg";
        String fileDownloadUri = "/suguconnect/files/download/" + fileName;

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
        assertEquals(fileDownloadUri, result.getPhotoUrl());
        assertNotNull(result.getDateAjout());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
        verify(fileStorageService, times(1)).storeFile(photo);
    }

    @Test
    public void testCreerCategorieWithPhotoUploadFailure() throws IOException {
        // Arrange
        String libelle = "Fruits";
        MultipartFile photo = mock(MultipartFile.class);

        when(photo.isEmpty()).thenReturn(false);
        when(fileStorageService.storeFile(photo)).thenThrow(new IOException("Upload failed"));
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
        // Photo URL should be null since upload failed
        assertNull(result.getPhotoUrl());
        assertNotNull(result.getDateAjout());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
        verify(fileStorageService, times(1)).storeFile(photo);
    }

    @Test
    public void testCreerCategorieWithoutPhoto() throws IOException {
        // Arrange
        String libelle = "Fruits";

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
        assertNull(result.getPhotoUrl());
        assertNotNull(result.getDateAjout());
        verify(categorieRepository, times(1)).save(any(Categorie.class));
        verify(fileStorageService, never()).storeFile(any());
    }
}