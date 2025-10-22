# 🔐 Documentation du Système d'Authentification - SuguConnect

## 📋 Vue d'ensemble

Le système d'authentification de SuguConnect est basé sur **JWT (JSON Web Tokens)** et **Spring Security**, offrant une authentification sécurisée et stateless pour les trois types d'utilisateurs : **Admin**, **Producteur**, et **Consommateur**.

---

## 🏗️ Architecture du Système

### Composants Principaux

1. **JwtService** - Génération et validation des tokens JWT
2. **JwtAuthenticationFilter** - Filtre pour intercepter et valider les requêtes
3. **CustomUserDetailsService** - Chargement des détails utilisateur
4. **AuthService** - Logique métier d'authentification
5. **AuthController** - Points d'entrée API REST
6. **SecurityConfig** - Configuration de la sécurité Spring

---

## 🔑 Fonctionnalités Clés

### 1. Génération de Tokens JWT

- **Durée de vie** : 24 heures
- **Algorithme** : HS256 (HMAC with SHA-256)
- **Claims inclus** :
  - `subject` : Téléphone de l'utilisateur
  - `userId` : ID de l'utilisateur
  - `nom` : Nom de l'utilisateur
  - `prenom` : Prénom de l'utilisateur
  - `role` : Rôle (ADMIN, PRODUCTEUR, CONSOMMATEUR)
  - `iat` : Date d'émission
  - `exp` : Date d'expiration

### 2. Validation des Comptes

#### Producteurs
- Vérification du statut : `ACCEPTE`, `EN_ATTENTE`, `REFUSE`
- Seuls les producteurs avec statut `ACCEPTE` peuvent se connecter
- Les comptes `EN_ATTENTE` ou `REFUSE` sont bloqués

#### Tous les Utilisateurs
- Vérification du mot de passe avec **BCrypt**
- Vérification de l'état actif du compte
- Gestion des comptes verrouillés

### 3. Contrôle d'Accès Basé sur les Rôles (RBAC)

| Endpoint | ADMIN | PRODUCTEUR | CONSOMMATEUR | Public |
|----------|-------|------------|--------------|--------|
| `/auth/**` | ✅ | ✅ | ✅ | ✅ |
| `/admin/**` | ✅ | ❌ | ❌ | ❌ |
| `/producteur/**` | ✅ | ✅ | ❌ | ❌ |
| `/consommateur/**` | ✅ | ❌ | ✅ | ❌ |
| `/paiement/**` | ✅ | ❌ | ✅ | ❌ |
| `/notifications/**` | ✅ | ✅ | ✅ | ❌ |
| `/categorie/liste` | ✅ | ✅ | ✅ | ✅ |
| `/categorie/creer` | ✅ | ❌ | ❌ | ❌ |
| `/swagger-ui/**` | ✅ | ✅ | ✅ | ✅ |

---

## 📡 Endpoints API

### 1. Connexion Universelle

**POST** `/auth/login`

Permet à n'importe quel utilisateur de se connecter.

**Request Body:**
```json
{
  "telephone": "771234567",
  "motDePasse": "password123"
}
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "telephone": "771234567",
  "nom": "Diallo",
  "prenom": "Amadou",
  "role": "PRODUCTEUR",
  "expiresIn": 86400000
}
```

**Responses d'Erreur:**
- **404** : Aucun compte trouvé avec ce téléphone
- **400** : Mot de passe incorrect
- **403** : Compte en attente de validation ou désactivé

---

### 2. Connexion Admin

**POST** `/auth/login/admin`

Endpoint spécifique pour les administrateurs.

**Request Body:**
```json
{
  "telephone": "771111111",
  "motDePasse": "adminPassword"
}
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "telephone": "771111111",
  "nom": "Admin",
  "prenom": "Système",
  "role": "ADMIN",
  "expiresIn": 86400000
}
```

**Responses d'Erreur:**
- **404** : Compte administrateur non trouvé
- **400** : Identifiants incorrects

---

### 3. Connexion Producteur

**POST** `/auth/login/producteur`

Endpoint spécifique pour les producteurs. Le compte doit être **validé** (statut `ACCEPTE`).

**Request Body:**
```json
{
  "telephone": "772222222",
  "motDePasse": "producteurPass"
}
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 2,
  "telephone": "772222222",
  "nom": "Fall",
  "prenom": "Moussa",
  "role": "PRODUCTEUR",
  "expiresIn": 86400000
}
```

**Responses d'Erreur:**
- **404** : Compte producteur non trouvé
- **400** : Mot de passe incorrect
- **403** : Compte en attente de validation (statut `EN_ATTENTE`)
- **403** : Compte refusé (statut `REFUSE`)

---

### 4. Connexion Consommateur

**POST** `/auth/login/consommateur`

Endpoint spécifique pour les consommateurs.

**Request Body:**
```json
{
  "telephone": "773333333",
  "motDePasse": "consommateurPass"
}
```

**Response Success (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": 3,
  "telephone": "773333333",
  "nom": "Sow",
  "prenom": "Fatou",
  "role": "CONSOMMATEUR",
  "expiresIn": 86400000
}
```

**Responses d'Erreur:**
- **404** : Compte consommateur non trouvé
- **400** : Mot de passe incorrect

---

### 5. Validation de Token

**POST** `/auth/validate-token`

Vérifie si un token JWT est valide et non expiré.

**Query Parameters:**
- `token` : Le token JWT à valider
- `telephone` : Le téléphone de l'utilisateur

**Request Example:**
```
POST /auth/validate-token?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...&telephone=771234567
```

**Response Success (200):**
```json
{
  "valid": true,
  "message": "Token valide",
  "role": "PRODUCTEUR"
}
```

**Response Erreur (401):**
```json
{
  "error": "Token invalide ou expiré",
  "timestamp": "2025-01-21T14:30:00"
}
```

---

### 6. Obtenir les Informations de l'Utilisateur Connecté

**GET** `/auth/me`

Récupère les informations de l'utilisateur à partir du token JWT.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response Success (200):**
```json
{
  "telephone": "771234567",
  "role": "PRODUCTEUR",
  "message": "Utilisateur authentifié"
}
```

**Response Erreur (401):**
```json
{
  "error": "Token manquant ou format invalide",
  "timestamp": "2025-01-21T14:30:00"
}
```

---

## 🔒 Sécurité

### 1. Configuration Spring Security

- **Session Management** : `STATELESS` (sans session)
- **CSRF** : Désactivé (adapté pour les API REST avec JWT)
- **Password Encoding** : BCrypt avec force 10
- **Authentication Provider** : DaoAuthenticationProvider

### 2. JWT Authentication Filter

Le filtre `JwtAuthenticationFilter` intercepte **chaque requête** et :

1. Extrait le token du header `Authorization: Bearer <token>`
2. Valide le token avec `JwtService`
3. Charge les détails de l'utilisateur avec `CustomUserDetailsService`
4. Crée un `UsernamePasswordAuthenticationToken`
5. Met à jour le `SecurityContext` de Spring Security

### 3. Custom User Details Service

Charge les utilisateurs depuis la base de données et crée des objets `UserDetails` avec :

- **Username** : Téléphone
- **Password** : Mot de passe hashé (BCrypt)
- **Authorities** : `ROLE_ADMIN`, `ROLE_PRODUCTEUR`, `ROLE_CONSOMMATEUR`
- **Account Status** :
  - `accountLocked` : Basé sur le champ `actif`
  - `disabled` : Basé sur le champ `actif`

---

## 🚀 Utilisation

### 1. Connexion d'un Utilisateur

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "771234567",
    "motDePasse": "password123"
  }'
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsIm5vbSI6IkRpYWxsbyIsInByZW5vbSI6IkFtYWRvdSIsInJvbGUiOiJQUk9EVUNURVVSIiwic3ViIjoiNzcxMjM0NTY3IiwiaWF0IjoxNzA2MTgwNDAwLCJleHAiOjE3MDYyNjY4MDB9.signature",
  "tokenType": "Bearer",
  "userId": 1,
  "telephone": "771234567",
  "nom": "Diallo",
  "prenom": "Amadou",
  "role": "PRODUCTEUR",
  "expiresIn": 86400000
}
```

### 2. Utilisation du Token pour les Requêtes Suivantes

```bash
curl -X GET http://localhost:8080/producteur/liste-produits \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Vérification de l'Identité

```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🛠️ Flux d'Authentification

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. POST /auth/login
       │    {telephone, motDePasse}
       ▼
┌──────────────────┐
│ AuthController   │
└──────┬───────────┘
       │
       │ 2. authService.login()
       ▼
┌──────────────────┐
│   AuthService    │
└──────┬───────────┘
       │
       │ 3. Vérifier utilisateur
       │ 4. Valider mot de passe (BCrypt)
       │ 5. Vérifier statut (producteur)
       ▼
┌──────────────────┐
│   JwtService     │
└──────┬───────────┘
       │
       │ 6. Générer token JWT
       │    - Claims: userId, nom, prenom, role
       │    - Expiration: 24h
       ▼
┌──────────────────┐
│  AuthResponse    │
└──────┬───────────┘
       │
       │ 7. Retourner token + infos user
       ▼
┌─────────────┐
│   Client    │
│ (stocke le  │
│   token)    │
└─────────────┘
```

---

## 🔄 Flux de Validation de Requête

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. GET /producteur/produits
       │    Header: Authorization: Bearer <token>
       ▼
┌───────────────────────┐
│ JwtAuthenticationFilter│
└──────┬────────────────┘
       │
       │ 2. Extraire token du header
       ▼
┌──────────────────┐
│   JwtService     │
└──────┬───────────┘
       │
       │ 3. Valider token
       │    - Signature valide?
       │    - Token expiré?
       ▼
┌──────────────────────────┐
│ CustomUserDetailsService │
└──────┬───────────────────┘
       │
       │ 4. Charger utilisateur
       │    - Vérifier téléphone
       │    - Récupérer rôle
       ▼
┌──────────────────┐
│ SecurityContext  │
└──────┬───────────┘
       │
       │ 5. Créer Authentication
       │    - Principal: UserDetails
       │    - Authorities: ROLE_XXX
       ▼
┌──────────────────┐
│   Controller     │
│  (requête OK)    │
└──────────────────┘
```

---

## ⚙️ Configuration

### application.properties

```properties
# Aucune configuration spéciale nécessaire pour JWT
# La clé secrète est générée automatiquement par JwtService
```

### Variables d'Environnement (Optionnel)

Pour la production, il est recommandé d'externaliser la clé secrète :

```properties
jwt.secret=${JWT_SECRET:defaultSecretKeyForDevelopment}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

---

## 🧪 Tests avec Swagger UI

1. **Accéder à Swagger UI** : http://localhost:8080/swagger-ui.html

2. **Tester la connexion** :
   - Ouvrir `Authentification` → `POST /auth/login`
   - Cliquer sur "Try it out"
   - Entrer les identifiants :
     ```json
     {
       "telephone": "771234567",
       "motDePasse": "password123"
     }
     ```
   - Cliquer sur "Execute"
   - Copier le `token` de la réponse

3. **Autoriser les requêtes suivantes** :
   - Cliquer sur le bouton **"Authorize"** en haut à droite
   - Entrer : `Bearer <votre_token>`
   - Cliquer sur "Authorize"

4. **Tester les endpoints protégés** :
   - Tous les endpoints nécessitant une authentification fonctionneront maintenant

---

## 🔍 Gestion des Erreurs

### Erreurs d'Authentification

| Code | Erreur | Description |
|------|--------|-------------|
| 400 | Bad Request | Mot de passe incorrect |
| 401 | Unauthorized | Token invalide ou expiré |
| 403 | Forbidden | Compte désactivé ou en attente de validation |
| 404 | Not Found | Utilisateur non trouvé |
| 500 | Internal Server Error | Erreur serveur inattendue |

### Format des Erreurs

```json
{
  "error": "Message d'erreur descriptif",
  "timestamp": "2025-01-21T14:30:00"
}
```

---

## 📊 Exemples de Réponses

### Producteur en Attente de Validation

**Request:**
```json
{
  "telephone": "772222222",
  "motDePasse": "producteurPass"
}
```

**Response (403):**
```json
{
  "error": "Votre compte est en attente de validation par un administrateur",
  "timestamp": "2025-01-21T14:30:00"
}
```

### Token Expiré

**Request:**
```
GET /auth/me
Authorization: Bearer <expired_token>
```

**Response (401):**
```json
{
  "error": "Token invalide : JWT expired at 2025-01-20T14:30:00Z",
  "timestamp": "2025-01-21T14:30:00"
}
```

---

## 🎯 Bonnes Pratiques

### 1. Stockage du Token (Côté Client)

- **Mobile** : Utiliser le stockage sécurisé (Keychain iOS, Keystore Android)
- **Web** : Utiliser `localStorage` ou `sessionStorage`
- **Ne jamais** stocker dans les cookies sans le flag `httpOnly`

### 2. Rafraîchissement du Token

Actuellement, les tokens expirent après 24h. Pour une meilleure expérience :

- Implémenter un endpoint `/auth/refresh` pour renouveler le token
- Gérer l'expiration côté client (rediriger vers login)

### 3. Sécurité

- **HTTPS** : Toujours utiliser HTTPS en production
- **Validation** : Valider tous les inputs côté serveur
- **Rate Limiting** : Implémenter une limitation des tentatives de connexion
- **Logging** : Logger toutes les tentatives de connexion (succès et échecs)

---

## 📚 Ressources

- [JWT.io](https://jwt.io/) - Debugger JWT
- [Spring Security Documentation](https://docs.spring.io/spring-security/)
- [BCrypt Calculator](https://bcrypt-generator.com/) - Générer des hash BCrypt

---

## 📝 Notes Importantes

1. **Clé Secrète** : La clé JWT est générée automatiquement au démarrage. En production, utilisez une clé fixe et sécurisée.

2. **Expiration** : Les tokens expirent après 24h. Implémentez un système de refresh pour une meilleure UX.

3. **Statut Producteur** : Seuls les producteurs avec `statutProducteur = ACCEPTE` peuvent se connecter.

4. **Rôles** : Les rôles sont préfixés par `ROLE_` dans Spring Security (ex: `ROLE_ADMIN`).

5. **Session Stateless** : L'application ne maintient pas de session côté serveur (JWT uniquement).

---

**Dernière mise à jour** : 21 Janvier 2025  
**Version** : 1.0.0
