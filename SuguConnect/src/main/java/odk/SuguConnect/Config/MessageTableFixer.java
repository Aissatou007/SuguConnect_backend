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
            logger.warning("JdbcTemplate n'est pas disponible, correction des tables ignorée");
            return;
        }
        
        fixMessageTableColumns(); // Handles 'lu' and 'is_read'
        fixMessageIdColumn(); // Fixes 'id_message' column name
        createProduitPhotosTable(); // Creates 'produit_photos'
    }

    private void fixMessageIdColumn() {
        try {
            // Vérifier si la colonne id_message existe
            boolean idMessageExists = checkColumnExists("message", "id_message");
            // Vérifier si la colonne id existe (nom alternatif)
            boolean idExists = checkColumnExists("message", "id");
            
            if (idMessageExists) {
                logger.info("✅ La colonne 'id_message' existe déjà.");
            } else if (idExists) {
                // Si 'id' existe mais pas 'id_message', on peut soit renommer soit mettre à jour l'entité
                // Pour l'instant, on garde 'id' et on met à jour l'entité pour utiliser 'id'
                logger.info("⚠️ La colonne 'id' existe. L'entité Message utilise maintenant 'id' comme nom de colonne.");
            } else {
                logger.warning("⚠️ Aucune colonne ID trouvée dans la table message. Hibernate devrait la créer.");
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de vérifier la colonne ID de la table message: " + e.getMessage());
        }
    }

    private void fixMessageTableColumns() {
        try {
            boolean luExists = checkColumnExists("message", "lu");
            boolean isReadExists = checkColumnExists("message", "is_read");

            if (luExists && isReadExists) {
                logger.info("⚠️ Les deux colonnes 'lu' et 'is_read' existent dans la table 'message'. Migration des données et suppression de 'lu'.");
                // Migrer les données de 'lu' vers 'is_read' si nécessaire
                jdbcTemplate.update("UPDATE message SET is_read = lu WHERE is_read IS NULL");
                // Supprimer la colonne 'lu'
                jdbcTemplate.execute("ALTER TABLE message DROP COLUMN lu");
                logger.info("✅ Colonne 'lu' supprimée après migration vers 'is_read'.");
                fixSingleMessageColumn("is_read"); // Assurer que 'is_read' est correct
            } else if (luExists) {
                logger.info("Détection de la colonne 'lu'. Correction...");
                fixSingleMessageColumn("lu");
                // Renommer 'lu' en 'is_read' pour la cohérence
                logger.info("Renommage de la colonne 'lu' en 'is_read'...");
                jdbcTemplate.execute("ALTER TABLE message CHANGE COLUMN lu is_read BOOLEAN NOT NULL DEFAULT FALSE");
                logger.info("✅ Colonne 'lu' renommée en 'is_read' et corrigée.");
            } else if (isReadExists) {
                logger.info("Détection de la colonne 'is_read'. Correction...");
                fixSingleMessageColumn("is_read");
            } else {
                logger.warning("⚠️ Aucune des colonnes 'lu' ou 'is_read' n'existe dans la table 'message'. Hibernate devrait la créer.");
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de corriger automatiquement les colonnes de la table message: " + e.getMessage());
            logger.warning("⚠️ Veuillez vérifier manuellement le schéma de la table 'message'.");
        }
    }

    private boolean checkColumnExists(String tableName, String columnName) {
        String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                          "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
        return count != null && count > 0;
    }

    private void fixSingleMessageColumn(String columnName) {
        try {
            String checkSql = "SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS " +
                              "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'message' AND COLUMN_NAME = ?";
            String defaultValue = jdbcTemplate.queryForObject(checkSql, String.class, columnName);

            if (defaultValue == null || defaultValue.equals("NULL")) {
                logger.info("Correction de la table message : ajout de la valeur par défaut à la colonne '" + columnName + "'");
                String alterSql = "ALTER TABLE message MODIFY COLUMN " + columnName + " BOOLEAN NOT NULL DEFAULT FALSE";
                jdbcTemplate.execute(alterSql);
                String updateSql = "UPDATE message SET " + columnName + " = FALSE WHERE " + columnName + " IS NULL";
                int updated = jdbcTemplate.update(updateSql);
                logger.info("✅ Colonne '" + columnName + "' corrigée avec succès! " + updated + " messages mis à jour.");
            } else {
                logger.info("✅ La colonne '" + columnName + "' a déjà une valeur par défaut: " + defaultValue);
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de corriger automatiquement la colonne '" + columnName + "': " + e.getMessage());
        }
    }

    private void createProduitPhotosTable() {
        try {
            // Check if 'produit' table exists first
            String checkProduitTableSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                                          "WHERE TABLE_SCHEMA = DATABASE() " +
                                          "AND TABLE_NAME = 'produit'";
            Integer produitTableExists = jdbcTemplate.queryForObject(checkProduitTableSql, Integer.class);

            if (produitTableExists == null || produitTableExists == 0) {
                logger.warning("⚠️ La table produit n'existe pas encore. La table produit_photos sera créée par Hibernate.");
                return; // Let Hibernate create 'produit' and then 'produit_photos'
            }

            // Check if 'produit_photos' table exists
            String checkTableSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                                   "WHERE TABLE_SCHEMA = DATABASE() " +
                                   "AND TABLE_NAME = 'produit_photos'";
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);

            if (tableExists == null || tableExists == 0) {
                logger.info("Création de la table produit_photos...");
                String createTableSql = "CREATE TABLE IF NOT EXISTS produit_photos (" +
                                        "produit_id INT NOT NULL, " +
                                        "photo_url VARCHAR(500) NOT NULL, " +
                                        "PRIMARY KEY (produit_id, photo_url), " +
                                        "FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE CASCADE" +
                                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                jdbcTemplate.execute(createTableSql);
                logger.info("✅ Table produit_photos créée avec succès!");
            } else {
                logger.info("✅ La table produit_photos existe déjà.");
            }
        } catch (Exception e) {
            logger.warning("⚠️ Impossible de créer automatiquement la table produit_photos: " + e.getMessage());
            logger.warning("⚠️ Veuillez exécuter manuellement le script SQL pour créer la table produit_photos");
            logger.warning("⚠️ Erreur: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}

