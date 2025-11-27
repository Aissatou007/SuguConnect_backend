package odk.SuguConnect.Config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import odk.SuguConnect.Enums.StatutCommande;

@Converter
public class StatutCommandeConverter implements AttributeConverter<StatutCommande, String> {

    @Override
    public String convertToDatabaseColumn(StatutCommande statut) {
        if (statut == null) {
            return null;
        }
        return statut.name();
    }

    @Override
    public StatutCommande convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            System.err.println("StatutCommande NULL ou vide détecté, utilisation de EN_ATTENTE par défaut");
            return StatutCommande.EN_ATTENTE; // Valeur par défaut
        }

        // Normaliser la chaîne (enlever espaces, mettre en majuscules)
        String normalized = dbData.trim().toUpperCase().replace(" ", "_");

        try {
            // Essayer de convertir directement
            return StatutCommande.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Si la valeur n'existe pas, mapper les valeurs connues
            System.err.println("Valeur StatutCommande inconnue détectée: '" + dbData + "' (normalisée: '" + normalized + "')");
            return mapToValidStatut(normalized);
        }
    }

    private StatutCommande mapToValidStatut(String value) {
        // Mapper les valeurs alternatives vers les valeurs valides
        switch (value) {
            case "REFUSEE":
            case "REFUSÉE":
            case "REFUSE":
                return StatutCommande.REFUSEE;
            case "ANNULEE":
            case "ANNULÉE":
            case "ANNULE":
            case "CANCELLED":
                return StatutCommande.ANNULEE;
            case "EN_COURS":
            case "EN_COUR":
            case "IN_PROGRESS":
                return StatutCommande.EN_LIVRAISON;
            case "VALIDEE":
            case "VALIDÉE":
            case "VALID":
            case "APPROVED":
                return StatutCommande.VALIDEE;
            case "LIVREE":
            case "LIVRÉE":
            case "DELIVERED":
                return StatutCommande.LIVREE;
            case "DECLINEE":
            case "DÉCLINÉE":
            case "DECLINE":
            case "REJECTED":
                return StatutCommande.DECLINEE;
            case "EN_ATTENTE":
            case "PENDING":
            case "WAITING":
                return StatutCommande.EN_ATTENTE;
            default:
                // Si aucune correspondance, retourner EN_ATTENTE par défaut
                System.err.println("Valeur de statut_commande inconnue: '" + value + "'. Utilisation de EN_ATTENTE par défaut.");
                return StatutCommande.EN_ATTENTE;
        }
    }
}

