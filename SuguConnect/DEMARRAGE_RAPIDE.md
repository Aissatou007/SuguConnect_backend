# 🚀 Guide de Démarrage Rapide - SuguConnect

## ✅ Prérequis

- ✅ Java 17 installé
- ✅ MySQL installé et démarré
- ✅ Maven installé
- ✅ Bruno ou Postman (pour tester l'API)

---

## 📋 Étape 1: Configuration de la Base de Données

### Option A: Base de données sera créée automatiquement

L'application créera automatiquement la base de données `suguConnectDB` au démarrage.

**Vérifiez juste que MySQL est démarré:**
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
```

### Option B: Créer manuellement la base de données

```sql
CREATE DATABASE suguConnectDB;
```

---

## 📋 Étape 2: Démarrer l'Application

### Depuis l'IDE (Recommandé)

1. Ouvrez le projet dans votre IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Localisez `SuguConnectApplication.java`
3. Cliquez sur **Run** ou appuyez sur **Shift + F10** (IntelliJ)

### Depuis le Terminal

```bash
# Naviguer vers le dossier du projet
cd "c:\Users\PC\Documents\Mes projets\Spring\SuguConnect_backend\SuguConnect"

# Compiler et démarrer
mvn spring-boot:run
```

---

## 📋 Étape 3: Vérifier le Démarrage

### Messages de Succès

Vous devriez voir ces messages dans la console:

```
👤 Admin par défaut créé avec succès!
📂 5 catégories créées!
👥 2 producteurs créés et validés!
🛒 6 produits créés avec succès!
✅ Initialisation des données de test terminée!

Started SuguConnectApplication in X.XXX seconds
```

### URLs Importantes

| Service | URL |
|---------|-----|
| Application | http://localhost:8080/suguconnect |
| Swagger UI | http://localhost:8080/suguconnect/swagger-ui.html |
| API Docs | http://localhost:8080/suguconnect/v3/api-docs |

---

## 📋 Étape 4: Tester l'API

### Option A: Avec Bruno (Recommandé)

1. **Installer Bruno**
   - Téléchargez: https://www.usebruno.com/downloads

2. **Ouvrir la Collection**
   - Lancez Bruno
   - Cliquez sur "Open Collection"
   - Sélectionnez le dossier `Suguconnect`

3. **Tester**
   - Exécutez `Auth/1-login-admin.bru`
   - Le token est automatiquement sauvegardé
   - Exécutez `Admin/1-get-consommateurs.bru`

### Option B: Avec Swagger UI

1. **Ouvrez Swagger**
   ```
   http://localhost:8080/suguconnect/swagger-ui.html
   ```

2. **Se connecter**
   - Cherchez `POST /auth/login/admin`
   - Cliquez sur **Try it out**
   - Entrez:
     ```json
     {
       "telephone": "70000000",
       "motDePasse": "admin123"
     }
     ```
   - Cliquez sur **Execute**
   - **Copiez le token** de la réponse

3. **Autoriser**
   - Cliquez sur le bouton **Authorize** 🔓 en haut
   - Collez le token dans le champ (sans "Bearer", Swagger l'ajoute automatiquement)
   - Cliquez sur **Authorize** puis **Close**

4. **Tester un Endpoint Protégé**
   - Cherchez `GET /admin/consommateurs`
   - Cliquez sur **Try it out**
   - Cliquez sur **Execute**
   - Vous devriez voir la liste des consommateurs

### Option C: Avec cURL

```bash
# 1. Connexion Admin
curl -X POST http://localhost:8080/suguconnect/auth/login/admin \
  -H "Content-Type: application/json" \
  -d "{\"telephone\":\"70000000\",\"motDePasse\":\"admin123\"}"

# 2. Copier le token de la réponse, puis:
curl -X GET http://localhost:8080/suguconnect/admin/consommateurs \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI"
```

---

## 📊 Données Créées Automatiquement

### 👤 Comptes Utilisateurs

| Type | Téléphone | Mot de passe | Statut |
|------|-----------|--------------|--------|
| Admin | `70000000` | `admin123` | Actif |
| Producteur 1 | `76543210` | `producteur123` | Validé |
| Producteur 2 | `77654321` | `producteur123` | Validé |

### 📂 Catégories

1. Fruits
2. Légumes
3. Céréales
4. Produits Laitiers
5. Viandes

### 🛒 Produits

| Produit | Prix (FCFA) | Stock | Catégorie | Producteur |
|---------|-------------|-------|-----------|------------|
| Mangues Bio | 2500/kg | 100 kg | Fruits | Traoré Mamadou |
| Bananes Plantain | 1500/kg | 150 kg | Fruits | Traoré Mamadou |
| Oranges Douces | 1800/kg | 80 kg | Fruits | Traoré Mamadou |
| Tomates Fraîches | 1200/kg | 120 kg | Légumes | Coulibaly Fatou |
| Oignons Locaux | 800/kg | 200 kg | Légumes | Coulibaly Fatou |
| Carottes Bio | 1000/kg | 90 kg | Légumes | Coulibaly Fatou |

---

## 🧪 Tests Rapides

### Test 1: Endpoint Public (Sans Token)

```bash
curl http://localhost:8080/suguconnect/consommateur/produits
```

**Résultat attendu:** Liste de 6 produits

### Test 2: Endpoint Protégé (Avec Token)

```bash
# 1. Obtenir le token
TOKEN=$(curl -s -X POST http://localhost:8080/suguconnect/auth/login/admin \
  -H "Content-Type: application/json" \
  -d '{"telephone":"70000000","motDePasse":"admin123"}' \
  | jq -r '.token')

# 2. Utiliser le token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/suguconnect/admin/consommateurs
```

**Résultat attendu:** Liste des consommateurs (peut être vide si aucun consommateur inscrit)

---

## 🔍 Vérification de l'Installation

### Checklist

- [ ] MySQL est démarré
- [ ] Application démarrée sans erreurs
- [ ] Messages d'initialisation affichés
- [ ] Swagger UI accessible
- [ ] Token admin récupéré avec succès
- [ ] Endpoint public retourne des données
- [ ] Endpoint protégé accessible avec token

### En cas de problème

#### Erreur: Connection refused

**Cause:** MySQL n'est pas démarré

**Solution:**
```bash
# Windows
net start MySQL80

# Linux
sudo systemctl start mysql
```

#### Erreur: Access denied for user

**Cause:** Mauvais identifiants MySQL

**Solution:** Vérifiez `application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

#### Erreur: Port 8080 already in use

**Cause:** Le port 8080 est utilisé

**Solution:** Changez le port dans `application.properties`:
```properties
server.port=8081
```

#### Aucun produit retourné

**Cause:** Initialisation des données a échoué

**Solution:**
1. Vérifiez les logs de démarrage
2. Supprimez la base de données et redémarrez
3. Vérifiez qu'il n'y a pas d'erreurs de compilation

---

## 📚 Prochaines Étapes

1. **Consultez la documentation:**
   - `GUIDE_UTILISATION_API.md` - Guide d'utilisation complet
   - `GUIDE_TEST_API.md` - Guide de test détaillé
   - `SECURITY_FIXES.md` - Corrections de sécurité
   - `DATA_INITIALIZATION.md` - Initialisation des données

2. **Testez les endpoints:**
   - Inscription consommateur
   - Connexion producteur
   - Gestion des produits
   - Passage de commandes

3. **Explorez Swagger:**
   - Documentation interactive
   - Test des endpoints
   - Schémas des modèles

---

## 🎯 Workflows de Test Recommandés

### Workflow Admin

```
1. POST /auth/login/admin          → Connexion
2. GET  /admin/consommateurs       → Liste consommateurs
3. GET  /admin/producteurs         → Liste producteurs
4. GET  /admin/produits            → Liste produits
5. PUT  /admin/producteurs/{id}/statut  → Valider producteur
```

### Workflow Consommateur

```
1. POST /consommateur/inscription  → S'inscrire
2. POST /auth/login/consommateur   → Se connecter
3. GET  /consommateur/produits     → Voir produits
4. POST /consommateur/{id}/panier/ajouter/{idProduit}  → Ajouter au panier
5. POST /consommateur/{id}/commande  → Passer commande
```

### Workflow Producteur

```
1. POST /producteur/inscription    → S'inscrire
2. Attendre validation admin       → StatutProducteur.ACCEPTE
3. POST /auth/login/producteur     → Se connecter
4. POST /producteur/{id}/produit   → Ajouter produit
5. GET  /producteur/{id}/produit   → Voir mes produits
```

---

## 💡 Conseils

1. **Token Expiré?** 
   - Les tokens JWT expirent après 24h
   - Reconnectez-vous pour obtenir un nouveau token

2. **Erreur 403?**
   - Vérifiez le format: `Authorization: Bearer TOKEN`
   - Vérifiez que vous avez le bon rôle
   - Reconnectez-vous

3. **Données de test?**
   - Les données sont créées au premier démarrage
   - Pour réinitialiser: supprimez la base et redémarrez

4. **Utilisez Swagger!**
   - Interface graphique intuitive
   - Gestion automatique des tokens
   - Documentation complète

---

## 🎉 Félicitations!

Votre backend SuguConnect est maintenant opérationnel! 🚀

Pour toute question, consultez la documentation ou les fichiers de guide dans le projet.

**Happy Coding!** 💻
