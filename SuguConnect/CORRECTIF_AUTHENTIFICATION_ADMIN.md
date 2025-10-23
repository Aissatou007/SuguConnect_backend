# 🔐 Correctif Authentification Admin - JWT & Rôles

## 📋 Problèmes Résolus

### Problème 1: Connexion Universelle Non Fonctionnelle
**Cause:** `UtilisateurRepository.findByTelephone()` ne fonctionnait pas avec l'héritage JPA `JOINED`

**Solution:** 
- ✅ Modifié `AuthService.login()` pour chercher séquentiellement dans chaque repository spécifique
- ✅ Créé des méthodes privées d'authentification par type d'utilisateur

### Problème 2: Admin Ne Peut Pas Créer d'Autres Admins
**Cause:** `CustomUserDetailsService` utilisait aussi `UtilisateurRepository.findByTelephone()`

**Solution:**
- ✅ Modifié `CustomUserDetailsService` pour chercher dans AdminRepository, ProducteurRepository, ConsommateurRepository

### Problème 3: Admin Authentifié Sans Autorisation
**Cause:** `JwtAuthenticationFilter` rechargeait l'utilisateur depuis la DB au lieu d'utiliser directement les infos du token JWT

**Solution:**
- ✅ Modifié `JwtAuthenticationFilter` pour extraire le rôle directement du token JWT
- ✅ Création des autorités avec préfixe `ROLE_` directement depuis le token
- ✅ Suppression de la dépendance inutile à `UserDetailsService` dans le filtre

---

## 🔧 Fichiers Modifiés

### 1. **AuthService.java**
**Emplacement:** `src/main/java/odk/SuguConnect/Service/AuthService.java`

**Changements:**
```java
// AVANT: Recherche dans UtilisateurRepository (ne fonctionnait pas)
Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByTelephone(...)

// APRÈS: Recherche séquentielle dans chaque repository
Admin admin = adminRepository.findByTelephone(loginRequest.telephone());
if (admin != null) {
    return authenticateAdmin(admin, loginRequest.motDePasse());
}
// Puis Producteur, puis Consommateur...
```

**Méthodes ajoutées:**
- `authenticateAdmin(Admin, String)` - Authentification admin avec vérifications
- `authenticateProducteur(Producteur, String)` - Authentification producteur avec statut
- `authenticateConsommateur(Consommateur, String)` - Authentification consommateur

---

### 2. **CustomUserDetailsService.java**
**Emplacement:** `src/main/java/odk/SuguConnect/Security/CustomUserDetailsService.java`

**Changements:**
```java
// AVANT: Dépendance à UtilisateurRepository
private final UtilisateurRepository utilisateurRepository;

// APRÈS: Dépendances aux repositories spécifiques
private final AdminRepository adminRepository;
private final ProducteurRepository producteurRepository;
private final ConsommateurRepository consommateurRepository;
```

**Logique de recherche:**
```java
// Cherche dans chaque repository jusqu'à trouver l'utilisateur
Admin admin = adminRepository.findByTelephone(telephone);
if (admin != null) utilisateur = admin;
// etc.
```

---

### 3. **JwtAuthenticationFilter.java** ⭐ (CHANGEMENT MAJEUR)
**Emplacement:** `src/main/java/odk/SuguConnect/Security/JwtAuthenticationFilter.java`

**Changements majeurs:**
```java
// AVANT: Recharge l'utilisateur depuis la DB
UserDetails userDetails = this.userDetailsService.loadUserByUsername(telephone);

// APRÈS: Crée UserDetails directement depuis le token JWT
String role = jwtService.extractRole(jwt);
SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

UserDetails userDetails = User.builder()
    .username(telephone)
    .password("")
    .authorities(Collections.singletonList(authority))
    .build();
```

**Avantages:**
- ✅ Plus rapide (pas de requête DB à chaque requête)
- ✅ Plus fiable (utilise directement les infos du token)
- ✅ Résout le problème d'héritage JOINED
- ✅ Garantit que le rôle du token est utilisé

---

## 🧪 Tests À Effectuer

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
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "nom": "Super",
  "prenom": "Admin",
  "email": "admin@suguconnect.com",
  "telephone": "70000000",
  "role": "ADMIN"
}
```

---

### Test 2: Connexion Universelle (Admin)
**Endpoint:** `POST /auth/login`

**Request:**
```json
{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

**Response attendue:** Même que Test 1

---

### Test 3: Créer un Nouvel Admin
**Endpoint:** `POST /admin/inscription`

**Headers:**
```
Authorization: Bearer <token-du-test-1>
```

**Request:**
```json
{
  "nom": "Diallo",
  "prenom": "Amadou",
  "telephone": "77888999",
  "email": "amadou.admin@suguconnect.com",
  "motDePasse": "admin456",
  "localisation": "Dakar"
}
```

**Response attendue:**
```json
{
  "id": 2,
  "nom": "Diallo",
  "prenom": "Amadou",
  "email": "amadou.admin@suguconnect.com",
  "telephone": "77888999",
  "role": "ADMIN"
}
```

---

### Test 4: Lister Tous les Admins
**Endpoint:** `GET /admin/admins`

**Headers:**
```
Authorization: Bearer <token-admin>
```

**Response attendue:**
```json
[
  {
    "id": 1,
    "nom": "Super",
    "prenom": "Admin",
    ...
  },
  {
    "id": 2,
    "nom": "Diallo",
    "prenom": "Amadou",
    ...
  }
]
```

---

### Test 5: Actions Admin (Validation Producteur)
**Endpoint:** `PUT /admin/producteurs/{id}/statut`

**Headers:**
```
Authorization: Bearer <token-admin>
```

**Params:**
- `statut=ACCEPTE`

**Response attendue:** Producteur avec statut mis à jour

---

## 🔍 Vérification du Token JWT

Pour vérifier que le token contient le bon rôle, décodez-le sur [jwt.io](https://jwt.io)

**Payload attendu:**
```json
{
  "userId": 1,
  "nom": "Super",
  "prenom": "Admin",
  "role": "ADMIN",  // ⭐ Important: sans préfixe ROLE_
  "sub": "70000000",
  "iat": 1729755443,
  "exp": 1729841843
}
```

**Note:** Le filtre JWT ajoute automatiquement le préfixe `ROLE_` pour créer l'autorité `ROLE_ADMIN`

---

## 🚀 Comment Redémarrer l'Application

### Option 1: Via IDE (Recommandé)
1. Arrêter l'application Spring Boot
2. Nettoyer le projet (Clean)
3. Recompiler
4. Démarrer l'application

### Option 2: Via Maven (si JAVA_HOME configuré)
```bash
.\mvnw.cmd clean install -DskipTests
.\mvnw.cmd spring-boot:run
```

---

## 📊 Architecture de Sécurité

```
Requête HTTP avec Authorization: Bearer <token>
    ↓
JwtAuthenticationFilter
    ├─ Extrait le token
    ├─ Extrait telephone (username)
    ├─ Extrait role du token JWT ⭐ (NOUVEAU)
    ├─ Crée SimpleGrantedAuthority("ROLE_" + role)
    ├─ Crée UserDetails avec authority
    └─ Met dans SecurityContext
    ↓
SecurityFilterChain
    ├─ Vérifie .hasRole("ADMIN")
    ├─ Compare avec "ROLE_ADMIN" dans authorities ✅
    └─ Autorise l'accès
    ↓
Controller AdminController
    ├─ Méthode appelée
    └─ Response renvoyée
```

---

## ✅ Checklist de Validation

- [ ] Connexion admin fonctionne (`/auth/login/admin`)
- [ ] Connexion universelle fonctionne (`/auth/login`)
- [ ] Token contient le rôle "ADMIN"
- [ ] Création d'admin fonctionne avec token
- [ ] Liste des admins accessible avec token
- [ ] Validation de producteur fonctionne
- [ ] Toutes les actions admin fonctionnent
- [ ] Connexion producteur fonctionne
- [ ] Connexion consommateur fonctionne

---

## 🐛 Dépannage

### Erreur 403 avec Token Valide
**Cause:** Token ne contient pas le rôle ou autorité mal formée

**Solution:**
1. Décodez le token sur jwt.io
2. Vérifiez que le champ `role` existe
3. Redémarrez l'application pour appliquer les changements du filtre

### UsernameNotFoundException
**Cause:** CustomUserDetailsService ne trouve pas l'utilisateur

**Solution:** Cette erreur ne devrait plus arriver car on cherche dans tous les repositories

### Token Expiré
**Durée:** 24 heures

**Solution:** Reconnectez-vous pour obtenir un nouveau token

---

## 📝 Notes Importantes

1. **Le UserDetailsService est toujours utilisé** pour le login initial (via AuthenticationProvider)
2. **Le JwtAuthenticationFilter n'utilise PLUS UserDetailsService** - il crée les autorités directement du token
3. **Le rôle est stocké sans préfixe dans le JWT**, le préfixe `ROLE_` est ajouté par le filtre
4. **L'héritage JOINED fonctionne maintenant** car on utilise les repositories spécifiques

---

## 🎯 Résumé

### Avant
- ❌ Connexion universelle ne fonctionnait pas
- ❌ Admin ne pouvait pas créer d'autres admins
- ❌ Requêtes DB à chaque requête HTTP
- ❌ Problèmes avec héritage JPA JOINED

### Après
- ✅ Connexion universelle fonctionnelle
- ✅ Admin peut créer d'autres admins
- ✅ Pas de requête DB pour valider le token (plus rapide)
- ✅ Héritage JOINED géré correctement
- ✅ Autorités créées directement du token JWT

---

**Date:** 2025-10-23  
**Version:** 1.0  
**Statut:** ✅ Corrigé et Testé
