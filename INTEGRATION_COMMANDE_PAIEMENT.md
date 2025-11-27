# Endpoint d'Intégration Commande et Paiement

## Endpoint Principal

### POST `/consommateur/{idConsommateur}/commande`

**Description :** Crée une commande et gère automatiquement le paiement associé.

**URL :** `http://localhost:8080/suguconnect/consommateur/{idConsommateur}/commande`

**Méthode :** `POST`

**Authentification :** Requise (JWT Token dans le header `Authorization: Bearer <token>`)

---

## Structure de la Requête

### Headers
```
Content-Type: application/json
Authorization: Bearer <votre_token_jwt>
```

### Path Parameters
- `idConsommateur` (int, requis) : ID du consommateur qui passe la commande

### Body (JSON)
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
  "modePaiement": "ORANGE_MONEY",
  "numeroTelephone": "70123456"
}
```

### Champs du Body

| Champ | Type | Requis | Description |
|-------|------|--------|-------------|
| `produits` | Array | ✅ Oui | Liste des produits du panier à commander |
| `produits[].produitId` | int | ✅ Oui | ID du produit |
| `produits[].quantite` | int | ✅ Oui | Quantité à commander |
| `modePaiement` | String | ✅ Oui | Mode de paiement : `ESPECES`, `ORANGE_MONEY`, `WAVE`, `MOBILE_MONEY`, `MOOV_MONEY` |
| `numeroTelephone` | String | Conditionnel | Requis si `modePaiement` est mobile (ORANGE_MONEY, WAVE, MOBILE_MONEY, MOOV_MONEY). Non requis pour ESPECES |

---

## Exemples d'Utilisation

### 1. Paiement en Espèces

```bash
POST /consommateur/9/commande
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "produits": [
    {
      "produitId": 1,
      "quantite": 2
    }
  ],
  "modePaiement": "ESPECES"
}
```

**Résultat :**
- ✅ Commande créée avec statut `EN_ATTENTE`
- ✅ Paiement créé avec statut `VALIDE` (validé automatiquement)
- ✅ Produits retirés du panier

---

### 2. Paiement Mobile (Orange Money)

```bash
POST /consommateur/9/commande
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

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
  "modePaiement": "ORANGE_MONEY",
  "numeroTelephone": "70123456"
}
```

**Résultat :**
- ✅ Commande créée avec statut `EN_ATTENTE`
- ✅ Paiement créé avec statut `EN_ATTENTE` (initié automatiquement)
- ✅ Notification envoyée au consommateur pour confirmer le paiement
- ✅ Produits retirés du panier
- ⏳ Le paiement sera validé automatiquement via webhook quand le consommateur confirme

---

### 3. Paiement Mobile (Wave)

```bash
POST /consommateur/9/commande
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "produits": [
    {
      "produitId": 5,
      "quantite": 3
    }
  ],
  "modePaiement": "WAVE",
  "numeroTelephone": "70123456"
}
```

---

### 4. Paiement Mobile (Moov Money)

```bash
POST /consommateur/9/commande
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "produits": [
    {
      "produitId": 5,
      "quantite": 3
    }
  ],
  "modePaiement": "MOOV_MONEY",
  "numeroTelephone": "70123456"
}
```

---

## Réponse Succès (200 OK)

```json
{
  "idCommande": 123,
  "montantTotal": 5000.0,
  "statutCommande": "EN_ATTENTE",
  "modePaiement": "ORANGE_MONEY",
  "dateCommande": "2025-11-25",
  "motifRejet": null,
  "receptionValidee": false,
  "dateReceptionValidee": null,
  "commandeProduits": [
    {
      "id": 1,
      "produit": {
        "id": 1,
        "nom": "Tomates",
        "prixUnitaire": 1500.0,
        "producteurId": 2,
        "producteurNom": "Doe",
        "producteurPrenom": "John"
      },
      "quantite": 2,
      "prixUnitaire": 1500.0
    }
  ],
  "consommateur": {
    "id": 9,
    "nom": "Dupont",
    "prenom": "Jean",
    "telephone": "70123456",
    "email": "jean@example.com",
    "localisation": "Cotonou",
    "longitude": 2.3158,
    "latitude": 6.4969,
    "role": "CONSOMMATEUR"
  },
  "paiement": {
    "idPaiement": 45,
    "montant": 5000.0,
    "datePaiement": "2025-11-25",
    "methodePaiement": "ORANGE_MONEY",
    "statutPaiement": "EN_ATTENTE"
  }
}
```

---

## Codes de Réponse

| Code | Description |
|------|-------------|
| `200` | Commande créée avec succès |
| `400` | Données invalides (stock insuffisant, numéro téléphone manquant pour paiement mobile, etc.) |
| `401` | Non authentifié (token manquant ou invalide) |
| `404` | Consommateur non trouvé |

---

## Erreurs Possibles

### 400 - Stock Insuffisant
```json
{
  "error": "Stock insuffisant pour Tomates. Disponible: 5, Demandé: 10"
}
```

### 400 - Numéro Téléphone Manquant
```json
{
  "error": "Le numéro de téléphone est requis pour les paiements mobiles"
}
```

### 400 - Produit Non Trouvé dans le Panier
```json
{
  "error": "Produit introuvable dans le panier"
}
```

### 404 - Consommateur Non Trouvé
```json
{
  "error": "Consommateur introuvable"
}
```

---

## Flux de Paiement Automatique

### Pour Paiement en Espèces
```
1. POST /consommateur/{id}/commande avec modePaiement: "ESPECES"
   ↓
2. Commande créée
   ↓
3. Paiement créé avec statut: VALIDE (automatique)
   ↓
4. ✅ Commande prête à être traitée
```

### Pour Paiement Mobile
```
1. POST /consommateur/{id}/commande avec modePaiement: "ORANGE_MONEY" + numeroTelephone
   ↓
2. Commande créée
   ↓
3. Paiement créé avec statut: EN_ATTENTE
   ↓
4. Initiation automatique du paiement mobile
   ↓
5. Notification envoyée au consommateur
   ↓
6. Consommateur confirme le paiement (via USSD/App)
   ↓
7. Webhook reçoit confirmation
   ↓
8. Paiement validé automatiquement (statut: VALIDE)
   ↓
9. ✅ Commande prête à être traitée
```

---

## Endpoints Complémentaires

### Récupérer le Paiement d'une Commande
```
GET /paiement/commande/{commandeId}
```

### Valider Manuellement un Paiement (Admin)
```
PUT /paiement/{id}/valider?referenceTransaction=REF123
```

### Marquer un Paiement comme Échoué
```
PUT /paiement/{id}/echouer?motifEchec=Transaction annulée
```

---

## Notes Importantes

1. **Paiement Automatique** : Le paiement est créé et traité automatiquement lors de la création de la commande. Aucun appel séparé n'est nécessaire.

2. **Numéro de Téléphone** : Obligatoire uniquement pour les paiements mobiles (ORANGE_MONEY, WAVE, MOBILE_MONEY, MOOV_MONEY). Non requis pour ESPECES.

3. **Paiement en Espèces** : Validé automatiquement, aucun webhook nécessaire.

4. **Paiement Mobile** : Le statut passe de `EN_ATTENTE` à `VALIDE` automatiquement via webhook quand le consommateur confirme le paiement.

5. **Produits du Panier** : Les produits commandés sont automatiquement retirés du panier après la création de la commande.

---

## Exemple cURL

```bash
curl -X POST "http://localhost:8080/suguconnect/consommateur/9/commande" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "produits": [
      {
        "produitId": 1,
        "quantite": 2
      }
    ],
    "modePaiement": "ORANGE_MONEY",
    "numeroTelephone": "70123456"
  }'
```

---

## Exemple JavaScript/Fetch

```javascript
const response = await fetch('http://localhost:8080/suguconnect/consommateur/9/commande', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    produits: [
      { produitId: 1, quantite: 2 },
      { produitId: 3, quantite: 1 }
    ],
    modePaiement: 'ORANGE_MONEY',
    numeroTelephone: '70123456'
  })
});

const commande = await response.json();
console.log('Commande créée:', commande);
```

---

## Exemple Flutter/Dart

```dart
final response = await http.post(
  Uri.parse('http://localhost:8080/suguconnect/consommateur/9/commande'),
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer $token',
  },
  body: jsonEncode({
    'produits': [
      {'produitId': 1, 'quantite': 2},
      {'produitId': 3, 'quantite': 1},
    ],
    'modePaiement': 'ORANGE_MONEY',
    'numeroTelephone': '70123456',
  }),
);

final commande = jsonDecode(response.body);
print('Commande créée: $commande');
```

