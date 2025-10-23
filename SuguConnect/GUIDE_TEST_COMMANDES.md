# 🛒 Guide de Test - Système de Commandes et Paiements

## 📋 Table des Matières
1. [Problèmes Résolus](#problèmes-résolus)
2. [Démarrage Rapide](#démarrage-rapide)
3. [Scénario de Test Complet](#scénario-de-test-complet)
4. [Endpoints Disponibles](#endpoints-disponibles)
5. [Dépannage](#dépannage)

---

## ✅ Problèmes Résolus

### 1. **Consommateur ne pouvait pas voir les produits**
- **Problème**: Conflit de configuration de sécurité
- **Solution**: Endpoint `/consommateur/produits` maintenant accessible publiquement (GET uniquement)

### 2. **Consommateur ne pouvait pas ajouter des produits au panier**
- **Problème**: Le système réduisait le stock lors de l'ajout au panier au lieu de lors de la commande
- **Solution**: 
  - Utilisation correcte de l'entité `PanierProduit` pour gérer les quantités
  - Stock réduit uniquement lors de la validation de la commande
  - Panier vidé automatiquement après la commande

### 3. **Paiements non fonctionnels**
- **Problème**: Conflits de contrôleurs de paiement en double
- **Solution**: Suppression des contrôleurs en double dans le package DTO/SimpleDTO

### 4. **Gestion incorrecte des quantités**
- **Problème**: Impossible de sélectionner la quantité voulue
- **Solution**: Système de panier complètement refait avec support de quantités multiples

---

## 🚀 Démarrage Rapide

### 1. Nettoyer et Démarrer l'Application

Si vous n'avez pas encore nettoyé le dossier `target`, faites-le maintenant :

```bash
# Supprimer le dossier target
Remove-Item -Path "target" -Recurse -Force

# Démarrer l'application via votre IDE
# OU via Maven (si configuré):
# mvn spring-boot:run
```

### 2. Accéder à Swagger

Une fois l'application démarrée, ouvrez Swagger :

```
http://localhost:8080/suguconnect/swagger-ui.html
```

---

## 🧪 Scénario de Test Complet

### Étape 1: Créer un Compte Consommateur

**Endpoint**: `POST /consommateur/inscription`

```json
{
  "nom": "Diallo",
  "prenom": "Aissata",
  "telephone": "78123456",
  "email": "aissata@test.com",
  "motDePasse": "password123",
  "localisation": "Dakar"
}
```

**Réponse attendue**: `200 OK` - "Soyez le bienvenue"

---

### Étape 2: Se Connecter

**Endpoint**: `POST /auth/login`

```json
{
  "telephone": "78123456",
  "motDePasse": "password123"
}
```

**Réponse attendue**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 3,
  "telephone": "78123456",
  "nom": "Diallo",
  "prenom": "Aissata",
  "role": "CONSOMMATEUR",
  "expiresIn": 86400000
}
```

**⚠️ IMPORTANT**: Copiez le `token` et le `userId` pour les étapes suivantes.

**Dans Swagger**: Cliquez sur le bouton **Authorize** 🔒 en haut à droite et collez le token.

---

### Étape 3: Consulter les Produits Disponibles

**Endpoint**: `GET /consommateur/produits` (Public - Pas de token requis)

**Réponse attendue**: Liste de produits avec stock disponible

```json
[
  {
    "id": 1,
    "nom": "Mangues Bio",
    "description": "Mangues fraîches et biologiques",
    "prixUnitaire": 2500.0,
    "stockDisponible": 100,
    "unite": "KILOGRAMME",
    "categorie": {...},
    "producteur": {...}
  },
  {
    "id": 2,
    "nom": "Bananes Plantain",
    "prixUnitaire": 1500.0,
    "stockDisponible": 150,
    ...
  }
]
```

**📝 Note**: Notez les IDs des produits que vous voulez commander.

---

### Étape 4: Ajouter des Produits au Panier

**Endpoint**: `POST /consommateur/{idConsommateur}/panier/ajouter/{idProduit}`

**Exemple**: Ajouter 5 kg de Mangues (produit ID=1) pour le consommateur ID=3

**URL**: `/consommateur/3/panier/ajouter/1?quantite=5`

**Réponse attendue**: `200 OK` - "Produit Mangues Bio ajouté au panier avec succès. Quantité: 5"

**Répétez** pour ajouter d'autres produits :

- **Bananes** (ID=2): `/consommateur/3/panier/ajouter/2?quantite=3`
- **Tomates** (ID=4): `/consommateur/3/panier/ajouter/4?quantite=10`

---

### Étape 5: Consulter le Panier

**Endpoint**: `GET /consommateur/{idConsommateur}/panier`

**URL**: `/consommateur/3/panier`

**Réponse attendue**:
```json
{
  "id": 1,
  "panierProduits": [
    {
      "id": 1,
      "produit": {
        "id": 1,
        "nom": "Mangues Bio",
        "prixUnitaire": 2500.0
      },
      "quantite": 5,
      "prixUnitaire": 2500.0
    },
    {
      "id": 2,
      "produit": {
        "id": 2,
        "nom": "Bananes Plantain",
        "prixUnitaire": 1500.0
      },
      "quantite": 3,
      "prixUnitaire": 1500.0
    }
  ]
}
```

**💰 Calcul du total**: 
- Mangues: 5 × 2500 = 12 500 FCFA
- Bananes: 3 × 1500 = 4 500 FCFA
- **Total**: 17 000 FCFA

---

### Étape 6: Passer la Commande

**Endpoint**: `POST /consommateur/{idConsommateur}/commande`

**URL**: `/consommateur/3/commande?modePaiement=ORANGE_MONEY`

**Modes de paiement disponibles**:
- `ORANGE_MONEY`
- `WAVE`
- `ESPECES`

**Réponse attendue**: Objet `Commande` complet avec:
```json
{
  "idCommande": 1,
  "dateCommande": "2025-10-22",
  "statutCommande": "EN_ATTENTE",
  "modePaiement": "ORANGE_MONEY",
  "montantTotal": 17000.0,
  "commandeProduits": [
    {
      "produit": {...},
      "quantite": 5,
      "prixUnitaire": 2500.0
    },
    {
      "produit": {...},
      "quantite": 3,
      "prixUnitaire": 1500.0
    }
  ],
  "paiement": {
    "idPaiement": 1,
    "montant": 17000.0,
    "statutPaiement": "INITIE",
    "methodePaiement": "ORANGE_MONEY",
    "datePaiement": "2025-10-22"
  },
  "consommateur": {...}
}
```

**✅ Vérifications automatiques**:
- ✅ Panier vidé automatiquement
- ✅ Stock réduit pour chaque produit
- ✅ Paiement créé avec statut `INITIE`
- ✅ Notification envoyée au consommateur
- ✅ Notification envoyée au producteur

---

### Étape 7: Consulter l'Historique des Commandes

**Endpoint**: `GET /consommateur/{idConsommateur}/commandes`

**URL**: `/consommateur/3/commandes`

**Réponse attendue**: Liste de toutes les commandes du consommateur

```json
[
  {
    "idCommande": 1,
    "dateCommande": "2025-10-22",
    "statutCommande": "EN_ATTENTE",
    "montantTotal": 17000.0,
    ...
  }
]
```

---

### Étape 8: Vérifier le Panier (Doit être vide)

**Endpoint**: `GET /consommateur/{idConsommateur}/panier`

**URL**: `/consommateur/3/panier`

**Réponse attendue**: `404 Not Found` - "Le panier est vide"

✅ **C'est normal !** Le panier a été vidé après la commande.

---

### Étape 9: Vérifier les Notifications

**Endpoint**: `GET /notifications/utilisateur/{utilisateurId}`

**URL pour le consommateur**: `/notifications/utilisateur/3`

**Réponse attendue**: Notification de commande passée
```json
[
  {
    "id": 1,
    "typeMessage": "COMMANDE_PASSEE",
    "message": "Votre commande #1 d'un montant de 17000.00 FCFA a été passée avec succès",
    "lienAction": "/commandes/1",
    "lu": false,
    "dateEnvoi": "2025-10-22T10:30:00"
  }
]
```

**URL pour le producteur** (ID récupéré de la réponse produit): `/notifications/utilisateur/1`

**Réponse attendue**: Notification de nouvelle commande
```json
[
  {
    "id": 2,
    "typeMessage": "COMMANDE_PASSEE",
    "message": "Nouvelle commande #1 reçue d'un montant de 17000.00 FCFA",
    "lienAction": "/commandes/1",
    "lu": false,
    "dateEnvoi": "2025-10-22T10:30:00"
  }
]
```

---

## 📚 Endpoints Disponibles

### **Consultation Publique** (Pas de token requis)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/consommateur/produits` | Voir tous les produits disponibles |
| `GET` | `/categorie` | Voir toutes les catégories |
| `GET` | `/categorie/{id}/produits` | Voir les produits d'une catégorie |
| `GET` | `/producteur/producteurs` | Voir tous les producteurs |
| `GET` | `/producteur/{id}` | Voir un producteur spécifique |

---

### **Gestion du Panier** (Token CONSOMMATEUR requis)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/consommateur/{id}/panier` | Consulter son panier |
| `POST` | `/consommateur/{id}/panier/ajouter/{produitId}?quantite={qty}` | Ajouter un produit |
| `DELETE` | `/consommateur/{id}/panier/retirer/{produitId}` | Retirer un produit |

---

### **Gestion des Commandes** (Token CONSOMMATEUR requis)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/consommateur/{id}/commande?modePaiement={mode}` | Passer une commande |
| `GET` | `/consommateur/{id}/commandes` | Voir l'historique des commandes |
| `POST` | `/consommateur/commande/{commandeId}/valider-reception?consommateurId={id}` | Valider la réception |

---

### **Gestion Producteur** (Token PRODUCTEUR requis)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `PUT` | `/producteur/commande/{commandeId}/statut?producteurId={id}&nouveauStatut={statut}` | Changer le statut d'une commande |

**Statuts disponibles**: `VALIDEE`, `DECLINEE`, `EN_LIVRAISON`, `LIVREE`

---

### **Avis/Reviews** (Token CONSOMMATEUR requis)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/avis/commande/{commandeId}?consommateurId={id}&note={1-5}&commentaire={text}` | Donner un avis après validation |
| `GET` | `/avis/producteur/{producteurId}` | Voir les avis d'un producteur |
| `GET` | `/avis/producteur/{producteurId}/moyenne` | Voir la moyenne des notes |

---

## 🔧 Dépannage

### Erreur: "Panier vide"
**Cause**: Vous essayez de passer une commande sans avoir ajouté de produits au panier.

**Solution**: 
1. Ajoutez d'abord des produits au panier avec `POST /consommateur/{id}/panier/ajouter/{produitId}`
2. Vérifiez votre panier avec `GET /consommateur/{id}/panier`
3. Puis passez la commande

---

### Erreur: "Stock insuffisant"
**Cause**: La quantité demandée dépasse le stock disponible.

**Solution**:
1. Vérifiez le stock disponible dans `GET /consommateur/produits`
2. Réduisez la quantité demandée
3. Ou choisissez un autre produit

---

### Erreur: "403 Forbidden"
**Cause**: Token manquant ou invalide

**Solution**:
1. Connectez-vous avec `POST /auth/login`
2. Copiez le token de la réponse
3. Dans Swagger, cliquez sur **Authorize** 🔒
4. Collez le token (sans "Bearer")
5. Cliquez sur **Authorize**

---

### Erreur: "404 - Consommateur introuvable"
**Cause**: Vous utilisez un mauvais ID de consommateur

**Solution**:
1. Après la connexion, utilisez le `userId` de la réponse
2. Ou récupérez votre ID avec `GET /consommateur/{id}`

---

### Le panier ne se vide pas après la commande
**Cause**: Ancien code en cache

**Solution**:
1. Supprimez le dossier `target`
2. Redémarrez l'application
3. Réessayez

---

## 🎯 Workflow Complet d'une Commande

```
1. CONSOMMATEUR: Inscription/Connexion
   ↓
2. CONSOMMATEUR: Consulter produits disponibles (PUBLIC)
   ↓
3. CONSOMMATEUR: Ajouter produits au panier (avec quantités)
   ↓
4. CONSOMMATEUR: Consulter le panier (vérifier)
   ↓
5. CONSOMMATEUR: Passer la commande
   ├─→ Stock réduit automatiquement
   ├─→ Panier vidé automatiquement
   ├─→ Paiement créé (INITIE)
   ├─→ Notification envoyée au consommateur
   └─→ Notification envoyée au producteur
   ↓
6. PRODUCTEUR: Reçoit notification
   ↓
7. PRODUCTEUR: Valide ou refuse la commande
   └─→ Notification envoyée au consommateur
   ↓
8. PRODUCTEUR: Passe en livraison
   └─→ Notification envoyée au consommateur
   ↓
9. PRODUCTEUR: Marque comme livrée
   └─→ Notification envoyée au consommateur
   ↓
10. CONSOMMATEUR: Valide la réception
    └─→ Notification de revenu envoyée au producteur
    ↓
11. CONSOMMATEUR: Donne un avis/note
```

---

## 🌟 Données de Test Pré-créées

L'application crée automatiquement au démarrage :

### Admin
- **Téléphone**: `70000000`
- **Mot de passe**: `admin123`

### Producteurs
1. **Mamadou Traoré**
   - Téléphone: `76543210`
   - Mot de passe: `producteur123`
   - Produits: Mangues, Bananes, Oranges

2. **Fatou Coulibaly**
   - Téléphone: `77654321`
   - Mot de passe: `producteur123`
   - Produits: Tomates, Oignons, Carottes

### Catégories
- Fruits
- Légumes
- Céréales
- Produits Laitiers
- Viandes

### Produits (Exemples)
- Mangues Bio (2500 FCFA/kg, Stock: 100)
- Bananes Plantain (1500 FCFA/kg, Stock: 150)
- Oranges Douces (1800 FCFA/kg, Stock: 80)
- Tomates Fraîches (1200 FCFA/kg, Stock: 120)

---

## 📞 Support

Si vous rencontrez d'autres problèmes :
1. Vérifiez les logs de l'application dans la console
2. Vérifiez que la base de données est bien créée
3. Assurez-vous que le port 8080 est disponible
4. Nettoyez le dossier `target` et redémarrez

---

**✨ Bon Test ! ✨**
