# 🔐 Guide d'Utilisation de l'API SuguConnect

## 📋 Table des matières
1. [Endpoints Publics](#endpoints-publics)
2. [Authentification JWT](#authentification-jwt)
3. [Endpoints Protégés](#endpoints-protégés)
4. [Exemples Pratiques](#exemples-pratiques)

---

## 🌍 Endpoints Publics

Ces endpoints sont accessibles **sans authentification**.

### 1. **Inscription**

#### Inscription Consommateur
```http
POST /suguconnect/consommateur/inscription
Content-Type: application/json

{
  "nom": "Adama",
  "prenom": "Dembele",
  "telephone": "94907946",
  "email": "adama@example.com",
  "localisation": "Lafiagougou",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "Astring12!"
}
```

#### Inscription Producteur
```http
POST /suguconnect/producteur/inscription
Content-Type: application/json

{
  "nom": "Traore",
  "prenom": "Mamadou",
  "telephone": "76543210",
  "email": "mamadou@example.com",
  "localisation": "Bamako",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "SecurePass123!",
  "description": "Producteur de fruits biologiques"
}
```

### 2. **Consultation Publique**

#### Voir tous les produits disponibles
```http
GET /suguconnect/consommateur/produits
```

#### Voir toutes les catégories
```http
GET /suguconnect/categorie
```

#### Voir une catégorie spécifique
```http
GET /suguconnect/categorie/{id}
```

#### Voir les produits d'une catégorie
```http
GET /suguconnect/categorie/{id}/produits
```

#### Voir tous les producteurs
```http
GET /suguconnect/producteur/producteurs
```

#### Voir un producteur spécifique
```http
GET /suguconnect/producteur/{id}
```

---

## 🔐 Authentification JWT

### Comment s'authentifier

#### 1. **Connexion Admin**
```http
POST /suguconnect/auth/login/admin
Content-Type: application/json

{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

#### 2. **Connexion Producteur**
```http
POST /suguconnect/auth/login/producteur
Content-Type: application/json

{
  "telephone": "76543210",
  "motDePasse": "SecurePass123!"
}
```

**Note:** Le compte producteur doit être **validé par un admin** avant de pouvoir se connecter.

#### 3. **Connexion Consommateur**
```http
POST /suguconnect/auth/login/consommateur
Content-Type: application/json

{
  "telephone": "94907946",
  "motDePasse": "Astring12!"
}
```

#### 4. **Connexion Universelle**
```http
POST /suguconnect/auth/login
Content-Type: application/json

{
  "telephone": "VOTRE_TELEPHONE",
  "motDePasse": "VOTRE_MOT_DE_PASSE"
}
```

### Réponse d'Authentification

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "nom": "Adama",
  "prenom": "Dembele",
  "email": "adama@example.com",
  "telephone": "94907946",
  "role": "CONSOMMATEUR"
}
```

**⚠️ Important:** Copiez le `token` pour l'utiliser dans les requêtes suivantes.

---

## 🔒 Endpoints Protégés

Pour accéder aux endpoints protégés, vous devez **inclure le token JWT** dans le header `Authorization`.

### Format du Header
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📝 Exemples Pratiques

### Exemple 1: Inscription + Connexion + Accès Protégé (Consommateur)

#### Étape 1: S'inscrire
```http
POST /suguconnect/consommateur/inscription
Content-Type: application/json

{
  "nom": "Adama",
  "prenom": "Dembele",
  "telephone": "94907946",
  "email": "adama@example.com",
  "localisation": "Lafiagougou",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "Astring12!"
}
```

**Réponse:**
```json
"Soyez le bienvenue "
```

#### Étape 2: Se connecter
```http
POST /suguconnect/auth/login/consommateur
Content-Type: application/json

{
  "telephone": "94907946",
  "motDePasse": "Astring12!"
}
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5NDkwNzk0NiIsInJvbGUiOiJDT05TT01NQVRFVVIiLCJpZCI6MSwiZXhwIjoxNzM1MTIzNDU2fQ.abcdef123456",
  "id": 1,
  "nom": "Adama",
  "prenom": "Dembele",
  "email": "adama@example.com",
  "telephone": "94907946",
  "role": "CONSOMMATEUR"
}
```

#### Étape 3: Ajouter un produit au panier (Protégé)
```http
POST /suguconnect/consommateur/1/panier/ajouter/5?quantite=2
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5NDkwNzk0NiIsInJvbGUiOiJDT05TT01NQVRFVVIiLCJpZCI6MSwiZXhwIjoxNzM1MTIzNDU2fQ.abcdef123456
```

---

### Exemple 2: Accès Admin

#### Étape 1: Se connecter en tant qu'Admin
```http
POST /suguconnect/auth/login/admin
Content-Type: application/json

{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.admin_token_here",
  "id": 1,
  "nom": "Super",
  "prenom": "Admin",
  "email": "admin@suguconnect.com",
  "telephone": "70000000",
  "role": "ADMIN"
}
```

#### Étape 2: Voir tous les consommateurs (Protégé - Admin uniquement)
```http
GET /suguconnect/admin/consommateurs
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.admin_token_here
```

#### Étape 3: Valider un producteur (Protégé - Admin uniquement)
```http
PUT /suguconnect/admin/producteurs/2/statut?statut=ACCEPTE
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.admin_token_here
```

---

## 🚨 Erreurs Courantes

### Erreur 403 Forbidden
**Cause:** Vous essayez d'accéder à un endpoint protégé sans token ou avec un token invalide.

**Solution:** 
1. Connectez-vous via `/auth/login/*`
2. Copiez le token reçu
3. Ajoutez-le dans le header: `Authorization: Bearer {token}`

### Erreur 401 Unauthorized
**Cause:** Token expiré ou invalide.

**Solution:** Reconnectez-vous pour obtenir un nouveau token.

### Erreur 400 Bad Request
**Cause:** Données manquantes ou format incorrect.

**Solution:** Vérifiez que tous les champs requis sont présents et correctement formatés.

---

## 📚 Rôles et Permissions

| Endpoint | Public | Consommateur | Producteur | Admin |
|----------|--------|--------------|------------|-------|
| `/consommateur/inscription` | ✅ | - | - | - |
| `/producteur/inscription` | ✅ | - | - | - |
| `/consommateur/produits` | ✅ | ✅ | ✅ | ✅ |
| `/auth/login/*` | ✅ | - | - | - |
| `/consommateur/**` | ❌ | ✅ | ❌ | ✅ |
| `/producteur/**` | ❌ | ❌ | ✅ | ✅ |
| `/admin/**` | ❌ | ❌ | ❌ | ✅ |
| `/categorie` (GET) | ✅ | ✅ | ✅ | ✅ |
| `/categorie` (POST/PUT/DELETE) | ❌ | ❌ | ❌ | ✅ |

---

## 🔗 Documentation Swagger

Accédez à la documentation interactive Swagger:
```
http://localhost:8080/suguconnect/swagger-ui.html
```

---

## 💡 Conseils

1. **Conservez votre token JWT** - Il est valide pendant une durée limitée
2. **Ne partagez jamais votre token** - C'est comme votre mot de passe
3. **Utilisez HTTPS en production** - Pour sécuriser les communications
4. **Testez d'abord avec Swagger** - Interface interactive pour tester l'API
5. **Vérifiez les rôles requis** - Avant d'appeler un endpoint

---

## 📞 Support

Pour toute question ou problème, consultez:
- Documentation Swagger: `/swagger-ui.html`
- Fichier `AUTHENTICATION_DOCUMENTATION.md`
- Fichier `NOTIFICATIONS_DOCUMENTATION.md`
