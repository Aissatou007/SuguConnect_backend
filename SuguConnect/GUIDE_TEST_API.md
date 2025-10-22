# 🧪 Guide Complet de Test - API SuguConnect

## ⚠️ IMPORTANT: Comment Envoyer le Token JWT

Lorsque vous avez un token JWT, vous **DEVEZ** l'envoyer dans le header `Authorization` de **CHAQUE** requête vers un endpoint protégé.

---

## 📋 Format du Header Authorization

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

**ATTENTION:**
- ✅ Le mot clé `Bearer` suivi d'un **espace**
- ✅ Ensuite le token complet (sans guillemets)
- ❌ NE PAS mettre de guillemets autour du token
- ❌ NE PAS oublier l'espace après `Bearer`

---

## 🔍 Test Complet Étape par Étape

### ÉTAPE 1: Connexion Admin (Obtenir le Token)

#### Requête
```http
POST http://localhost:8080/suguconnect/auth/login/admin
Content-Type: application/json

{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

#### Réponse Attendue (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co",
  "tokenType": "Bearer",
  "userId": 1,
  "nom": "Super",
  "prenom": "Admin",
  "email": "admin@suguconnect.com",
  "telephone": "70000000",
  "role": "ADMIN",
  "message": "Connexion réussie"
}
```

**👉 COPIEZ LE TOKEN (la valeur du champ "token")**

---

### ÉTAPE 2: Utiliser le Token pour Accéder aux Endpoints Protégés

#### 2.1 Récupérer Tous les Consommateurs

```http
GET http://localhost:8080/suguconnect/admin/consommateurs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

#### Réponse Attendue (200 OK)
```json
[
  {
    "id": 1,
    "nom": "Adama",
    "prenom": "Dembele",
    "telephone": "94907946",
    "email": "adama@example.com",
    "role": "CONSOMMATEUR"
  }
]
```

#### 2.2 Récupérer Tous les Producteurs

```http
GET http://localhost:8080/suguconnect/admin/producteurs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

#### 2.3 Récupérer Tous les Produits

```http
GET http://localhost:8080/suguconnect/admin/produits
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

#### 2.4 Récupérer Toutes les Commandes

```http
GET http://localhost:8080/suguconnect/admin/commandes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

#### 2.5 Récupérer Tous les Paiements

```http
GET http://localhost:8080/suguconnect/admin/paiements
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co
```

---

## 🛠️ Configuration dans Postman

### 1. Créer une Variable d'Environnement

1. Cliquez sur l'icône ⚙️ (Settings) en haut à droite
2. Créez un nouvel environnement nommé "SuguConnect"
3. Ajoutez ces variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| baseUrl | http://localhost:8080/suguconnect | http://localhost:8080/suguconnect |
| adminToken | (vide) | (vide) |

### 2. Script pour Capturer Automatiquement le Token

Dans votre requête de **login**, ajoutez ce script dans l'onglet **Tests**:

```javascript
// Capturer le token de la réponse
if (pm.response.code === 200) {
    const response = pm.response.json();
    
    // Sauvegarder le token dans la variable d'environnement
    pm.environment.set("adminToken", response.token);
    
    console.log("Token capturé avec succès!");
}
```

### 3. Utiliser le Token dans les Requêtes

Dans l'onglet **Authorization** de vos requêtes protégées:
- Type: **Bearer Token**
- Token: `{{adminToken}}`

OU dans l'onglet **Headers**:
- Key: `Authorization`
- Value: `Bearer {{adminToken}}`

---

## 🛠️ Configuration dans Bruno

### 1. Connexion Admin

Créez un fichier `login-admin.bru`:

```
meta {
  name: Login Admin
  type: http
  seq: 1
}

post {
  url: {{baseUrl}}/auth/login/admin
  body: json
  auth: none
}

body:json {
  {
    "telephone": "70000000",
    "motDePasse": "admin123"
  }
}

script:post-response {
  if (res.status === 200) {
    bru.setEnvVar("adminToken", res.body.token);
  }
}
```

### 2. Requête Protégée (Exemple: Liste Consommateurs)

Créez un fichier `get-consommateurs.bru`:

```
meta {
  name: Get All Consommateurs
  type: http
  seq: 2
}

get {
  url: {{baseUrl}}/admin/consommateurs
  body: none
  auth: bearer
}

auth:bearer {
  token: {{adminToken}}
}
```

### 3. Fichier d'Environnement `environments/local.bru`

```
vars {
  baseUrl: http://localhost:8080/suguconnect
  adminToken: 
}
```

---

## 🐛 Résolution des Problèmes

### Erreur 403 Forbidden

**Cause possible:**
1. ❌ Token manquant ou invalide
2. ❌ Format incorrect du header
3. ❌ Token expiré
4. ❌ Rôle insuffisant

**Solution:**
```
✅ Vérifiez que le header commence par "Bearer " (avec espace)
✅ Reconnectez-vous pour obtenir un nouveau token
✅ Vérifiez que vous utilisez le bon rôle (ADMIN pour /admin/**)
✅ Copiez le token COMPLET sans guillemets
```

### Erreur 401 Unauthorized

**Cause:** Token expiré ou invalide

**Solution:**
```bash
# Reconnectez-vous pour obtenir un nouveau token
POST /auth/login/admin
```

### Le Token N'est Pas Reconnu

**Vérifications:**
```
1. Le header s'appelle bien "Authorization" (avec A majuscule)
2. La valeur commence par "Bearer " (B majuscule + espace)
3. Le token est collé directement après l'espace
4. Pas de guillemets autour du token
5. Le token est complet (commence par "eyJ")
```

**Format CORRECT:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Formats INCORRECTS:**
```
❌ authorization: Bearer token
❌ Authorization: token
❌ Authorization: "Bearer token"
❌ Authorization: Bearer "token"
❌ Authorization:Bearer token (pas d'espace)
```

---

## 📝 Exemple Complet avec cURL

### 1. Connexion
```bash
curl -X POST http://localhost:8080/suguconnect/auth/login/admin \
  -H "Content-Type: application/json" \
  -d '{"telephone":"70000000","motDePasse":"admin123"}'
```

### 2. Utiliser le Token
```bash
curl -X GET http://localhost:8080/suguconnect/admin/consommateurs \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJ1c2VySWQiOjEsIm5vbSI6IlN1cGVyIiwicHJlbm9tIjoiQWRtaW4iLCJzdWIiOiI3MDAwMDAwMCIsImlhdCI6MTc2MTA1Mzg3NSwiZXhwIjoxNzYxMTQwMjc1fQ.UcD8ISFtE9PgTVjHiIC6ocvNTpWa1ZhxLmu3A8am0Co"
```

---

## 🎯 Checklist de Vérification

Avant de tester:
- [ ] Application Spring Boot est démarrée
- [ ] Base de données MySQL est accessible
- [ ] Le port 8080 n'est pas utilisé par une autre application
- [ ] Les données de test ont été initialisées

Pour chaque requête protégée:
- [ ] J'ai un token JWT valide
- [ ] Le header s'appelle "Authorization"
- [ ] La valeur commence par "Bearer "
- [ ] Le token est complet et sans guillemets
- [ ] Le token n'est pas expiré (valide 24h)

---

## 📚 Endpoints Admin Disponibles

| Endpoint | Méthode | Description | Token Requis |
|----------|---------|-------------|--------------|
| `/admin/consommateurs` | GET | Liste des consommateurs | ✅ ADMIN |
| `/admin/producteurs` | GET | Liste des producteurs | ✅ ADMIN |
| `/admin/produits` | GET | Liste des produits | ✅ ADMIN |
| `/admin/commandes` | GET | Liste des commandes | ✅ ADMIN |
| `/admin/paiements` | GET | Liste des paiements | ✅ ADMIN |
| `/admin/admins` | GET | Liste des admins | ✅ ADMIN |
| `/admin/inscription` | POST | Créer un admin | ✅ ADMIN |
| `/admin/producteurs/{id}/statut` | PUT | Valider un producteur | ✅ ADMIN |

---

## 💡 Conseils

1. **Token Expiré?** Reconnectez-vous pour obtenir un nouveau token
2. **Erreur 403?** Vérifiez le format du header Authorization
3. **Liste Vide?** Vérifiez que les données de test ont été créées
4. **Utilisez Swagger** pour tester facilement: http://localhost:8080/suguconnect/swagger-ui.html

---

## 🎉 Workflow Complet

```mermaid
graph LR
    A[POST /auth/login/admin] --> B[Récupérer Token]
    B --> C[Copier Token]
    C --> D[Ajouter Header Authorization]
    D --> E[GET /admin/consommateurs]
    E --> F[Recevoir Données]
```

**Bonne chance avec vos tests!** 🚀
