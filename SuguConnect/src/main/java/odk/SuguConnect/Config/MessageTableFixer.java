package odk.SuguConnect.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Classe pour corriger automatiquement la table message au démarrage
 * Ajoute une valeur par défaut à la colonne 'lu' si elle n'en a pas
 */
@Component
@Order(1)
public class MessageTableFixer {
    
    private static final Logger logger = Logger.getLogger(MessageTableFixer.class.getName());
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void fixMessageTable() {
        if (jdbcTemplate == null) {
            logger.warning("JdbcTemplate n'est pas disponible, correction de la table message ignorée");
            return;
        }
        
        try {
            // Vérifier si la colonne a déjà une valeur par défaut
            String checkSql = "SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'message' " +
                            "AND COLUMN_NAME = 'lu'";
            
            String defaultValue = jdbcTemplate.queryForObject(checkSql, String.class);
            
            if (defaultValue == null || defaultValue.equals("NULL")) {
                logger.info("Correction de la table message : ajout de la valeur par défaut à la colonne 'lu'");
                
                // Ajouter la valeur par défaut
                String alterSql = "ALTER TABLE message MODIFY COLUMN lu BOOLEAN NOT NULL DEFAULT FALSE";
                jdbcTemplate.execute(alterSql);
                
                // Mettre à jour les messages existants qui pourraient avoir une valeur NULL
                String updateSql = "UPDATE message SET lu = FALSE WHERE lu IS NULL";
                int updated = jdbcTemplate.update(updateSql);
                
                logger.info("✅ Table message corrigée avec succès! " + updated + " messages mis à jour.");
            } else {
                logger.info("✅ La colonne 'lu' a déjà une valeur par défaut: " + defaultValue);
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de corriger automatiquement la table message: " + e.getMessage());
            logger.warning("⚠️ Veuillez exécuter manuellement le script SQL: ALTER TABLE message MODIFY COLUMN lu BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }
}

