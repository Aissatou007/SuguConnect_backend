# 🔍 Test de Diagnostic - Création d'Admin

## ⚠️ PRÉREQUIS CRITIQUE
**AVEZ-VOUS REDÉMARRÉ L'APPLICATION ?**
Les modifications du JwtAuthenticationFilter ne prendront effet qu'après redémarrage complet de l'application Spring Boot.

---

## 📋 Tests à Effectuer dans l'Ordre

### Test 1: Connexion Admin
**Endpoint:** `POST /auth/login/admin`

**Request:**
```json
{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

**Response attendue:**
```json
{
  "token": "eyJhbGc...",
  "id": 1,
  "nom": "Super",
  "prenom": "Admin",
  "email": "admin@suguconnect.com",
  "telephone": "70000000",
  "role": "ADMIN"
}
```

✅ **Copiez le token reçu**

---

### Test 2: Debug Authentication (NOUVEAU - TRÈS IMPORTANT)
**Endpoint:** `GET /auth/debug-auth`

**Headers:**
```
Authorization: Bearer <votre-token-du-test-1>
```

**Response attendue:**
```json
{
  "authHeaderPresent": true,
  "authHeader": "Bearer eyJhbGc...",
  "tokenExtracted": true,
  "telephone": "70000000",
  "role": "ADMIN",
  "roleWithPrefix": "ROLE_ADMIN",
  "tokenValid": true,
  "springSecurityAuthenticated": true,
  "principal": "org.springframework.security.core.userdetails.User...",
  "authorities": "[ROLE_ADMIN]"
}
```

**❌ Si vous voyez `"springSecurityAuthenticated": false`** → L'application n'a pas été redémarrée ou le filtre JWT ne fonctionne pas.

**❌ Si vous voyez `"authorities": "[]"`** → Le rôle n'est pas ajouté correctement.

---

### Test 3: Vérifier les Informations Utilisateur
**Endpoint:** `GET /auth/me`

**Headers:**
```
Authorization: Bearer <votre-token>
```

**Response attendue:**
```json
{
  "telephone": "70000000",
  "role": "ADMIN",
  "roleWithPrefix": "ROLE_ADMIN",
  "message": "Utilisateur authentifié",
  "authenticated": true
}
```

---

### Test 4: Essayer de Créer un Admin
**Endpoint:** `POST /admin/inscription`

**Headers:**
```
Authorization: Bearer <votre-token>
```

**Request:**
```json
{
  "nom": "Diallo",
  "prenom": "Amadou",
  "telephone": "77999888",
  "email": "amadou.admin@suguconnect.com",
  "motDePasse": "admin456",
  "localisation": "Dakar"
}
```

**Si ça échoue, notez:**
- Code HTTP (403, 401, 500) ?
- Message d'erreur exact ?
- Timestamp de l'erreur ?

---

## 🐛 Diagnostics Possibles

### Erreur 403 Forbidden
**Cause possible:**
- Token présent mais rôle pas reconnu
- Application pas redémarrée
- SecurityContext vide

**Solution:**
1. Vérifiez le résultat de `/auth/debug-auth`
2. Si `springSecurityAuthenticated: false` → Redémarrez l'application
3. Si `authorities: []` → Problème avec le JwtAuthenticationFilter

---

### Erreur 401 Unauthorized
**Cause possible:**
- Token invalide ou mal formaté
- Header Authorization manquant

**Solution:**
1. Vérifiez le format: `Authorization: Bearer <token>` (avec espace après Bearer)
2. Vérifiez que le token n'est pas expiré (24h de validité)
3. Reconnectez-vous pour obtenir un nouveau token

---

### Erreur 500 Internal Server Error
**Cause possible:**
- Exception dans le code (AdminService.verifierRoleAdmin())
- Problème avec le repository

**Solution:**
1. Consultez les logs de l'application
2. Cherchez les stack traces
3. Envoyez-moi le message d'erreur complet

---

## 📊 Checklist de Vérification

- [ ] Application redémarrée après modifications du JwtAuthenticationFilter
- [ ] Login admin réussi (`/auth/login/admin`)
- [ ] Token copié correctement
- [ ] Test debug-auth effectué (`/auth/debug-auth`)
- [ ] `springSecurityAuthenticated: true` dans la réponse debug
- [ ] `authorities: "[ROLE_ADMIN]"` dans la réponse debug
- [ ] Header Authorization bien formaté: `Bearer <token>`
- [ ] Pas d'espace supplémentaire dans le token

---

## 🔧 Si Swagger UI est Utilisé

### Configuration de l'Authorization
1. Cliquez sur le bouton **"Authorize"** en haut à droite
2. Dans le champ, entrez **SEULEMENT le token** (sans "Bearer ")
3. Swagger ajoutera automatiquement le préfixe "Bearer "
4. Cliquez sur "Authorize"
5. Fermez la popup
6. Testez l'endpoint

### ⚠️ Erreur Commune avec Swagger
Si vous mettez `Bearer <token>` dans Swagger Authorize, il va envoyer `Bearer Bearer <token>` → ERREUR

**Correct:** Dans Swagger Authorize → `eyJhbGc...` (juste le token)
**Incorrect:** Dans Swagger Authorize → `Bearer eyJhbGc...`

---

## 📝 Format de Réponse à Me Fournir

```
TEST 1 - Login Admin:
✅ Réussi / ❌ Échoué
Token reçu: eyJhbG...

TEST 2 - Debug Auth:
{
  "springSecurityAuthenticated": ?,
  "authorities": "?",
  "role": "?",
  "roleWithPrefix": "?"
}

TEST 3 - Auth Me:
✅ Réussi / ❌ Échoué
Message: ?

TEST 4 - Créer Admin:
❌ Échoué
Code HTTP: ?
Message d'erreur: ?
```

---

## 🎯 Ce Que Je Cherche À Savoir

1. **Le JwtAuthenticationFilter fonctionne-t-il ?**
   → Réponse dans `/auth/debug-auth` avec `springSecurityAuthenticated: true`

2. **Le rôle est-il bien ajouté aux autorités ?**
   → Réponse dans `/auth/debug-auth` avec `authorities: "[ROLE_ADMIN]"`

3. **Quel est le message d'erreur exact ?**
   → Code HTTP et message lors de la tentative de création d'admin

---

## 🚀 Actions Immédiates

1. **REDÉMARREZ L'APPLICATION** (si pas déjà fait)
2. Effectuez les 4 tests ci-dessus
3. Copiez-collez les résultats (surtout `/auth/debug-auth`)
4. Envoyez-moi le code HTTP et message d'erreur de la création d'admin

Avec ces informations, je pourrai identifier précisément le problème !
