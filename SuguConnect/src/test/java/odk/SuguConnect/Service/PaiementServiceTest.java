package odk.SuguConnect.Service;

import odk.SuguConnect.Entity.Commande;
import odk.SuguConnect.Entity.Paiement;
import odk.SuguConnect.Entity.Produit;
import odk.SuguConnect.Entity.Producteur;
import odk.SuguConnect.Entity.CommandeProduit;
import odk.SuguConnect.Enums.StatutPaiement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaiementServiceTest {

    @Mock
    private odk.SuguConnect.Repository.PaiementRepository paiementRepository;

    @Mock
    private odk.SuguConnect.Repository.CommandeRepository commandeRepository;

    @Mock
    private odk.SuguConnect.Service.NotificationService notificationService;

    @Mock
    private odk.SuguConnect.Repository.ProduitRepository produitRepository;

    @InjectMocks
    private PaiementService paiementService;

    @Test
    public void testGetPaiementsRecusParProducteur() {
        // Arrange
        int producteurId = 1;
        
        Producteur producteur = new Producteur();
        producteur.setId(producteurId);
        
        Produit produit = new Produit();
        produit.setId(1);
        produit.setProducteur(producteur);
        
        CommandeProduit commandeProduit = new CommandeProduit();
        commandeProduit.setProduit(produit);
        
        Commande commande = new Commande();
        commande.setIdCommande(1);
        commande.setCommandeProduits(Arrays.asList(commandeProduit));
        
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(1);
        paiement.setCommande(commande);
        
        when(paiementRepository.findAll()).thenReturn(Arrays.asList(paiement));
        
        // Act
        List<Paiement> result = paiementService.getPaiementsRecusParProducteur(producteurId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getIdPaiement());
    }
}