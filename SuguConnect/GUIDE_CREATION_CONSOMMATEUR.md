# 🛒 Guide de Création de Compte Consommateur - Résolution Erreur JSON

## ❌ **Erreur JSON Mal Formé**
```
JSON parse error: Unexpected character ('}' (code 125)): was expecting double-quote to start field name
```

Cette erreur signifie que le JSON envoyé est **syntaxiquement incorrect**.

---

## ✅ **Structure Correcte du JSON**

```json
{
  "nom": "Diop",
  "prenom": "Amina",
  "telephone": "771234567",
  "email": "amina.diop@example.com",
  "localisation": "Dakar",
  "latitude": 14.6937,
  "longitude": -17.444,
  "motDePasse": "motdepasse123"
}
```

---

## ⚠️ **Erreurs Courantes et Solutions**

### ❌ Erreur 1: Virgule en trop à la fin
```json
{
  "nom": "Diop",
  "prenom": "Amina",
  "motDePasse": "123456",  // ← Virgule en trop ici
}
```
**Solution:** Retirer la virgule en trop
```json
{
  "nom": "Diop",
  "prenom": "Amina",
  "motDePasse": "123456"  // ✅ Pas de virgule à la fin
}
```

### ❌ Erreur 2: Guillemets manquants
```json
{
  nom: "Diop",  // ← Guillemets manquants
  prenom: "Amina"
}
```
**Solution:** Ajouter les guillemets
```json
{
  "nom": "Diop",  // ✅ Guillemets ajoutés
  "prenom": "Amina"
}
```

### ❌ Erreur 3: Guillemets simples au lieu de doubles
```json
{
  'nom': 'Diop',  // ← Guillemets simples
  'prenom': 'Amina'
}
```
**Solution:** Utiliser des guillemets doubles
```json
{
  "nom": "Diop",  // ✅ Guillemets doubles
  "prenom": "Amina"
}
```

### ❌ Erreur 4: Champs manquants
```json
{
  "nom": "Diop",
  "prenom": "Amina"
  // ← Champs telephone, motDePasse manquants
}
```
**Solution:** Inclure tous les champs requis
```json
{
  "nom": "Diop",
  "prenom": "Amina",
  "telephone": "771234567",
  "email": "amina@example.com",
  "localisation": "Dakar",
  "latitude": 14,
  "longitude": -17,
  "motDePasse": "123456"
}
```

---

## 🧪 **Test de Validation JSON**

### Endpoint:
```http
POST http://localhost:8080/suguconnect/consommateur/inscription
Content-Type: application/json
```

### JSON Valide (à copier-coller):
```json
{
  "nom": "Diop",
  "prenom": "Amina",
  "telephone": "771234567",
  "email": "amina.diop@example.com",
  "localisation": "Dakar",
  "latitude": 14,
  "longitude": -17,
  "motDePasse": "motdepasse123"
}
```

---

## 🔍 **Vérification en Ligne**

Utilisez un validateur JSON en ligne:
1. Copiez votre JSON
2. Collez-le sur [https://jsonlint.com/](https://jsonlint.com/)
3. Cliquez sur "Validate JSON"
4. Corrigez les erreurs indiquées

---

## 🛠️ **Dans Différents Outils**

### Postman:
1. Méthode: POST
2. URL: `http://localhost:8080/suguconnect/consommateur/inscription`
3. Onglet "Body" → "raw" → "JSON"
4. Collez le JSON valide

### Swagger UI:
1. Ouvrez l'endpoint POST `/consommateur/inscription`
2. Cliquez "Try it out"
3. Remplissez les champs dans l'interface
4. Swagger générera automatiquement le JSON

### cURL:
```bash
curl -X POST http://localhost:8080/suguconnect/consommateur/inscription \
  -H "Content-Type: application/json" \
  -d '{
  "nom": "Diop",
  "prenom": "Amina",
  "telephone": "771234567",
  "email": "amina.diop@example.com",
  "localisation": "Dakar",
  "latitude": 14,
  "longitude": -17,
  "motDePasse": "motdepasse123"
}'
```

---

## 📋 **Champs Requis**

| Champ | Type | Obligatoire | Exemple |
|-------|------|-------------|---------|
| `nom` | String | ✅ | "Diop" |
| `prenom` | String | ✅ | "Amina" |
| `telephone` | String | ✅ | "771234567" |
| `email` | String | ✅ | "amina@example.com" |
| `localisation` | String | ✅ | "Dakar" |
| `latitude` | long | ✅ | 14 |
| `longitude` | long | ✅ | -17 |
| `motDePasse` | String | ✅ | "motdepasse123" |

---

## ✅ **Exemple de Réponse Attendue**

**200 OK:**
```json
"Soyez le bienvenue "
```

**Erreurs possibles:**
- **400 Bad Request:** "Ce compte existe déjà"
- **400 Bad Request:** JSON mal formé (erreur que vous aviez)

---

## 🎯 **Conseils de Débogage**

1. **Validez toujours votre JSON** avant envoi
2. **Utilisez un éditeur avec coloration syntaxique** (VS Code, IntelliJ)
3. **Activez l'affichage des caractères invisibles** pour voir les espaces
4. **Copiez-collez depuis des exemples validés** plutôt que de taper manuellement

---

## 📞 **Support**

Si l'erreur persiste après validation du JSON:
1. Copiez-collez le JSON exact que vous envoyez
2. Notez le timestamp de l'erreur
3. Partagez les logs complets de l'application

**Dernière mise à jour:** 2025-10-23