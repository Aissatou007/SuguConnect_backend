package odk.SuguConnect.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
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
            // Vérifier quelle colonne existe : 'lu' ou 'is_read'
            String checkColumnSql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                                   "WHERE TABLE_SCHEMA = DATABASE() " +
                                   "AND TABLE_NAME = 'message' " +
                                   "AND (COLUMN_NAME = 'lu' OR COLUMN_NAME = 'is_read')";
            
            List<String> columns = jdbcTemplate.queryForList(checkColumnSql, String.class);
            
            String columnName = null;
            if (columns.contains("is_read")) {
                columnName = "is_read";
            } else if (columns.contains("lu")) {
                columnName = "lu";
            }
            
            if (columnName == null) {
                logger.warning("⚠️ Aucune colonne 'lu' ou 'is_read' trouvée dans la table message");
                return;
            }
            
            logger.info("Colonne trouvée: " + columnName);
            
            // Vérifier si la colonne a déjà une valeur par défaut
            String checkSql = "SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = 'message' " +
                            "AND COLUMN_NAME = ?";
            
            String defaultValue = jdbcTemplate.queryForObject(checkSql, String.class, columnName);
            
            if (defaultValue == null || defaultValue.equals("NULL") || defaultValue.isEmpty()) {
                logger.info("Correction de la table message : ajout de la valeur par défaut à la colonne '" + columnName + "'");
                
                // Ajouter la valeur par défaut
                String alterSql = "ALTER TABLE message MODIFY COLUMN " + columnName + " BOOLEAN NOT NULL DEFAULT FALSE";
                jdbcTemplate.execute(alterSql);
                
                // Mettre à jour les messages existants qui pourraient avoir une valeur NULL
                String updateSql = "UPDATE message SET " + columnName + " = FALSE WHERE " + columnName + " IS NULL";
                int updated = jdbcTemplate.update(updateSql);
                
                logger.info("✅ Table message corrigée avec succès! " + updated + " messages mis à jour.");
            } else {
                logger.info("✅ La colonne '" + columnName + "' a déjà une valeur par défaut: " + defaultValue);
            }
            
            // Si les deux colonnes existent, supprimer l'ancienne 'lu' si 'is_read' existe
            if (columns.contains("is_read") && columns.contains("lu")) {
                logger.info("⚠️ Les deux colonnes 'lu' et 'is_read' existent. Suppression de 'lu'...");
                try {
                    jdbcTemplate.execute("ALTER TABLE message DROP COLUMN lu");
                    logger.info("✅ Colonne 'lu' supprimée avec succès");
                } catch (Exception e) {
                    logger.warning("⚠️ Impossible de supprimer la colonne 'lu': " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de corriger automatiquement la table message: " + e.getMessage());
            logger.warning("⚠️ Veuillez exécuter manuellement le script SQL: ALTER TABLE message MODIFY COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE");
            e.printStackTrace();
        }
    }
}

