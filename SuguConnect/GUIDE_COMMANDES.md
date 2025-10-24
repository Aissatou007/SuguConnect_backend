# 🛒 Guide des Commandes - Deux Méthodes Disponibles

## 🎯 **Deux Façons de Passer une Commande**

SuguConnect offre maintenant **deux méthodes** pour passer une commande:

---

## 📦 **Méthode 1: Via le Panier (Ancienne)**

### Endpoint:
```http
POST /consommateur/{idConsommateur}/commande?modePaiement=ORANGE_MONEY
```

### Description:
- Utilise les produits déjà ajoutés au **panier**
- Plus simple pour les achats rapides
- Moins flexible

### Avantages:
✅ Plus rapide si vous avez déjà rempli votre panier
✅ Moins de données à envoyer

### Inconvénients:
❌ Moins flexible (vous devez utiliser le panier)
❌ Ne permet pas de choisir les produits au moment de la commande

---

## 🛍️ **Méthode 2: Directe (Nouvelle - Recommandée)**

### Endpoint:
```http
POST /consommateur/{idConsommateur}/commande/direct
```

### Description:
- Permet de **choisir les produits directement** lors de la commande
- Plus flexible et intuitive
- **Recommandée pour une meilleure expérience utilisateur**

### Avantages:
✅ ✅ Plus flexible - Choisissez vos produits au moment de commander
✅ ✅ Pas besoin de gérer le panier
✅ ✅ Plus intuitive pour les utilisateurs
✅ ✅ Moins d'étapes

### Inconvénients:
❌ Légèrement plus de données à envoyer

---

## 🚀 **Méthode Recommandée: Commande Directe**

### Request Body:
```json
{
  "produits": [
    {
      "produitId": 1,
      "quantite": 2
    },
    {
      "produitId": 3,
      "quantite": 1
    }
  ],
  "modePaiement": "ORANGE_MONEY"
}
```

### Exemple Complet:
```http
POST http://localhost:8080/suguconnect/consommateur/1/commande/direct
Content-Type: application/json
Authorization: Bearer <token-consommateur>

{
  "produits": [
    {
      "produitId": 1,
      "quantite": 2
    },
    {
      "produitId": 3,
      "quantite": 1
    }
  ],
  "modePaiement": "ORANGE_MONEY"
}
```

---

## 📋 **Modes de Paiement Disponibles**

### Valeurs possibles:
- **`ORANGE_MONEY`** - Paiement par Orange Money
- **`WAVE`** - Paiement par Wave
- **`ESPECES`** - Paiement en espèces à la livraison

---

## 🧪 **Exemple de Réponse**

### Succès (200 OK):
```json
{
  "idCommande": 15,
  "montantTotal": 7500.0,
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
    "id": 1,
    "nom": "Diop",
    "prenom": "Amina"
  }
}
```

---

## 🔄 **Migration de l'Ancienne Méthode**

### Ancienne méthode (via panier):
```http
POST /consommateur/1/commande?modePaiement=ORANGE_MONEY
```

### Nouvelle méthode (directe):
```http
POST /consommateur/1/commande/direct
Content-Type: application/json

{
  "produits": [
    {"produitId": 1, "quantite": 2}
  ],
  "modePaiement": "ORANGE_MONEY"
}
```

---

## 🎯 **Quand Utiliser Chaque Méthode**

### Utilisez la commande **directe** quand:
- ✅ Vous voulez une expérience utilisateur moderne
- ✅ Vous permettez aux utilisateurs de choisir les produits au moment de commander
- ✅ Vous développez une nouvelle interface utilisateur

### Utilisez la commande **via panier** quand:
- ✅ Vous avez déjà une interface basée sur le panier
- ✅ Vous voulez garder la compatibilité avec l'ancien système

---

## 🛠️ **Dans Swagger UI**

1. **Endpoint recommandé:** `POST /consommateur/{id}/commande/direct`
2. **RequestBody:** Remplissez les champs `produits` et `modePaiement`
3. **produits:** Array d'objets avec `produitId` et `quantite`

---

## 📞 **Support**

En cas de problème:
1. Vérifiez que les IDs de produits existent
2. Vérifiez que le stock est suffisant
3. Assurez-vous que le mode de paiement est valide
4. Vérifiez les autorisations (token valide)

**Dernière mise à jour:** 2025-10-23