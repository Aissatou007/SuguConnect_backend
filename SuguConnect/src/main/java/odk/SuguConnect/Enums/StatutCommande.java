package odk.SuguConnect.Enums;

public enum StatutCommande {
    EN_ATTENTE,
    EN_LIVRAISON,
    VALIDEE,
    LIVREE,
    DECLINEE,
    REFUSEE,  // Alias pour DECLINEE, utilisé dans certains contextes
    ANNULEE   // Pour les commandes annulées
}
