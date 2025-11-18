package odk.SuguConnect.Enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModePaiementTest {

    @Test
    public void testAllPaymentMethodsExist() {
        // Test that all expected payment methods exist
        assertNotNull(ModePaiement.ORANGE_MONEY);
        assertNotNull(ModePaiement.MOOV_MONEY);
        assertNotNull(ModePaiement.WAVE);
        assertNotNull(ModePaiement.ESPECES);
        
        // Test that we have exactly 4 payment methods
        assertEquals(4, ModePaiement.values().length);
    }
    
    @Test
    public void testPaymentMethodsToString() {
        // Test that the toString() method returns the expected values
        assertEquals("ORANGE_MONEY", ModePaiement.ORANGE_MONEY.toString());
        assertEquals("MOOV_MONEY", ModePaiement.MOOV_MONEY.toString());
        assertEquals("WAVE", ModePaiement.WAVE.toString());
        assertEquals("ESPECES", ModePaiement.ESPECES.toString());
    }
}