package odk.SuguConnect.Service;

import odk.SuguConnect.Repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduitServiceDeleteAllTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    public void testSupprimerTousLesProduits() {
        // Arrange
        long productCount = 5;
        when(produitRepository.count()).thenReturn(productCount);

        // Act
        String result = produitService.supprimerTousLesProduits();

        // Assert
        assertEquals("5 produits ont été supprimés", result);
        verify(produitRepository, times(1)).count();
        verify(produitRepository, times(1)).deleteAll();
    }
}