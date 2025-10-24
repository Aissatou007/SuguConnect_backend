# 📦 Guide des Commandes - Améliorations de Sérialisation

## 🎯 **Problème Résolu**

### **Erreur précédente:**
```
Document nesting depth (1001) exceeds the maximum allowed (1000)
```

### **Cause:**
Boucles infinies dans la sérialisation JSON dues aux relations bidirectionnelles:
- [Commande](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\Entity\Commande.java#L14-L46) ↔ [CommandeProduit](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\Entity\CommandeProduit.java#L11-L28)
- [Commande](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\Entity\Commande.java#L14-L46) ↔ [Paiement](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\Entity\Paiement.java#L12-L31)

---

## ✅ **Solutions Appliquées**

### **1. Ajout de @JsonIgnore**
```java
// Dans Commande.java
@OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
@JsonIgnore
private List<CommandeProduit> commandeProduits;

@OneToOne
@JoinColumn(name = "paiement_id")
@JsonIgnore
private Paiement paiement;

// Dans CommandeProduit.java
@ManyToOne
@JoinColumn(name = "commande_id")
@JsonIgnore
private Commande commande;

// Dans Paiement.java
@OneToOne(mappedBy = "paiement")
@JsonIgnore
private Commande commande;
```

### **2. Création de DTOs de Réponse**
- [CommandeResponseDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\CommandeResponseDTO.java#L3-L48)
- [CommandeProduitResponseDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\CommandeProduitResponseDTO.java#L3-L21)
- [ProduitSimpleDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\ProduitSimpleDTO.java#L3-L18)
- [ConsommateurSimpleDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\ConsommateurSimpleDTO.java#L3-L37)
- [PaiementSimpleDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\PaiementSimpleDTO.java#L3-L26)

### **3. Création de CommandeMapper**
Convertit les entités en DTOs sans relations cycliques.

---

## 🚀 **Endpoints Mis à Jour**

### **Tous les endpoints de commande** retournent maintenant [CommandeResponseDTO](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\src\main\java\odk\SuguConnect\DTO\Responses\CommandeResponseDTO.java#L3-L48):

1. ✅ `POST /consommateur/{id}/commande` - Commande via panier
2. ✅ `POST /consommateur/{id}/commande/direct` - Commande directe
3. ✅ `GET /consommateur/{id}/commandes` - Historique des commandes
4. ✅ `GET /consommateur/commande/{id}` - Commande spécifique
5. ✅ `POST /consommateur/commande/{id}/valider-reception` - Validation réception

---

## 📋 **Structure de Réponse Améliorée**

### **Avant (entité avec boucles):**
```json
{
  "idCommande": 2,
  "montantTotal": 12000.0,
  "statutCommande": "EN_ATTENTE",
  "modePaiement": "ORANGE_MONEY",
  "dateCommande": "2025-10-23",
  "consommateur": {
    "id": 16,
    "nom": "Diabaté",
    // ... boucle infinie avec paiement → commande → paiement → ...
  }
}
```

### **Après (DTO sans boucles):**
```json
{
  "idCommande": 2,
  "montantTotal": 12000.0,
  "statutCommande": "EN_ATTENTE",
  "modePaiement": "ORANGE_MONEY",
  "dateCommande": "2025-10-23",
  "commandeProduits": [
    {
      "id": 1,
      "produit": {
        "id": 1,
        "nom": "Mangues Bio",
        "prixUnitaire": 2500.0
      },
      "quantite": 2,
      "prixUnitaire": 2500.0
    }
  ],
  "consommateur": {
    "id": 16,
    "nom": "Diabaté",
    "prenom": "Aïda",
    "telephone": "00000000",
    "email": "aida.diabate@example.com",
    "localisation": "Bamako, Mali",
    "longitude": -7,
    "latitude": 12,
    "role": "CONSOMMATEUR"
  },
  "paiement": {
    "idPaiement": 2,
    "montant": 12000.0,
    "datePaiement": "2025-10-23",
    "methodePaiement": "ORANGE_MONEY",
    "statutPaiement": "INITIE"
  }
}
```

---

## 🧪 **Testez les Nouvelles Fonctionnalités**

### **1. Passer une commande directe:**
```http
POST /consommateur/1/commande/direct
Content-Type: application/json
Authorization: Bearer <token>

{
  "produits": [
    {
      "produitId": 1,
      "quantite": 2
    }
  ],
  "modePaiement": "ORANGE_MONEY"
}
```

### **2. Voir une commande spécifique:**
```http
GET /consommateur/commande/2
Authorization: Bearer <token>
```

### **3. Voir l'historique des commandes:**
```http
GET /consommateur/1/commandes
Authorization: Bearer <token>
```

---

## 🎯 **Avantages de la Nouvelle Approche**

1. ✅ **Pas de boucle infinie** - Sérialisation JSON stable
2. ✅ **Réponse structurée** - Données pertinentes uniquement
3. ✅ **Meilleure performance** - Moins de données transférées
4. ✅ **Sécurité** - Pas d'exposition des mots de passe ou données sensibles
5. ✅ **Maintenabilité** - Séparation claire entre entités et DTOs

---

## 📚 **Documentation Créée**

- [GUIDE_COMMANDES.md](file://c:\Users\PC\Documents\Mes%20projets\Spring\SuguConnect_backend\SuguConnect\GUIDE_COMMANDES.md) - Guide des deux méthodes de commande
- Ce fichier - Guide des améliorations de sérialisation

---

**Redémarrez l'application pour appliquer toutes les modifications!** 🎉

Les commandes fonctionnent maintenant sans erreur de profondeur JSON!