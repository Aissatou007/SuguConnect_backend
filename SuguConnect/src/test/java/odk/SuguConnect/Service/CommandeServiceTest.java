package odk.SuguConnect.Service;

import odk.SuguConnect.DTO.Request.ProduitCommandeDTO;
import odk.SuguConnect.Entity.*;
import odk.SuguConnect.Enums.ModePaiement;
import odk.SuguConnect.Repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ConsommateurRepository consommateurRepository;

    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private PanierRepository panierRepository;

    @Mock
    private PanierProduitRepository panierProduitRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommandeService commandeService;

    @Test
    public void testPasserCommandeWithEmptyProductList() {
        // Arrange
        int consommateurId = 1;
        Consommateur consommateur = new Consommateur();
        consommateur.setId(consommateurId);
        
        Panier panier = new Panier();
        panier.setId(1);
        panier.setConsommateur(consommateur);
        panier.setPanierProduits(Arrays.asList(new PanierProduit()));
        
        consommateur.setPanier(panier);
        
        odk.SuguConnect.DTO.Request.PasserCommandePanierDTO request = 
            new odk.SuguConnect.DTO.Request.PasserCommandePanierDTO(
                Collections.emptyList(), // Empty product list
                ModePaiement.ORANGE_MONEY
            );

        when(consommateurRepository.findById(consommateurId)).thenReturn(Optional.of(consommateur));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commandeService.passerCommande(consommateurId, request);
        });
        
        assertTrue(exception.getMessage().contains("ne peut pas être vide"));
    }
    
    @Test
    public void testPasserCommandeWithNullPanier() {
        // Arrange
        int consommateurId = 1;
        Consommateur consommateur = new Consommateur();
        consommateur.setId(consommateurId);
        // consommateur.setPanier(null); // Panier is null by default
        
        odk.SuguConnect.DTO.Request.PasserCommandePanierDTO request = 
            new odk.SuguConnect.DTO.Request.PasserCommandePanierDTO(
                Arrays.asList(new ProduitCommandeDTO(1, 2)),
                ModePaiement.ORANGE_MONEY
            );

        when(consommateurRepository.findById(consommateurId)).thenReturn(Optional.of(consommateur));

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            commandeService.passerCommande(consommateurId, request);
        });
        
        assertTrue(exception.getMessage().contains("Panier non trouvé"));
    }
}