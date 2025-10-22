package odk.SuguConnect.Enums;

public enum TypeMessage {
    // Notifications de commande
    COMMANDE_PASSEE("Commande passée", "Votre commande a été passée avec succès"),
    COMMANDE_VALIDEE("Commande validée", "Votre commande a été validée"),
    COMMANDE_REFUSEE("Commande refusée", "Votre commande a été refusée"),
    COMMANDE_EN_LIVRAISON("Commande en livraison", "Votre commande est en cours de livraison"),
    COMMANDE_LIVREE("Commande livrée", "Votre commande a été livrée"),

    // Notifications de paiement
    PAIEMENT_RECU("Paiement reçu", "Paiement reçu avec succès"),
    REMBOURSEMENT_EFFECTUE("Remboursement effectué", "Votre remboursement a été effectué"),
    REVENU_PRODUCTEUR("Revenu reçu", "Vous avez reçu le paiement de votre vente"),

    // Notifications de compte
    COMPTE_VALIDE("Compte validé", "Votre compte producteur a été validé"),
    COMPTE_REFUSE("Compte refusé", "Votre demande de compte producteur a été refusée"),

    // Notifications de stock
    STOCK_FAIBLE("Stock faible", "Le stock de votre produit est faible"),
    PRODUIT_EPUISE("Produit épuisé", "Votre produit est épuisé"),

    // Notifications système pour admin
    NOUVELLE_INSCRIPTION_PRODUCTEUR("Nouvelle inscription", "Un nouveau producteur s'est inscrit"),
    NOUVELLE_COMMANDE("Nouvelle commande", "Une nouvelle commande a été passée"),
    ACTIVITE_SUSPECTE("Activité suspecte", "Activité suspecte détectée"),
    INFO_SYSTEME("Information système", "Information importante du système");

    private final String titre;
    private final String description;

    TypeMessage(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }
}
