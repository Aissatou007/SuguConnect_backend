# 🔍 Validateur JSON - Guide Pratique

## 📋 **Structure Requise pour Consommateur**

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

## ✅ **Checklist de Validation**

### Avant d'envoyer:
- [ ] Tous les champs sont présents
- [ ] Toutes les clés sont entre guillemets doubles `"`
- [ ] Toutes les valeurs String sont entre guillemets doubles `"`
- [ ] Pas de virgule après le dernier champ
- [ ] Pas de guillemets simples `'`
- [ ] Pas de commentaires `//` ou `/* */`
- [ ] Pas de fonctions ou variables JavaScript

---

## 🛠️ **Outils de Validation**

### 1. En Ligne (Recommandé)
**URL:** https://jsonlint.com/

**Utilisation:**
1. Copiez votre JSON
2. Collez-le dans la zone de texte
3. Cliquez "Validate JSON"
4. Corrigez les erreurs indiquées

### 2. VS Code
**Extensions utiles:**
- JSON Tools
- JSONLint

**Validation automatique:**
- Les erreurs s'affichent en rouge dans la barre latérale

### 3. IntelliJ IDEA
**Validation automatique:**
- Coloration syntaxique immédiate
- Erreurs soulignées en rouge

---

## ⚠️ **Erreurs Fréquentes**

### 1. **Guillemets Manquants**
❌ INCORRECT:
```json
{
  nom: "Diop",
  prenom: "Amina"
}
```

✅ CORRECT:
```json
{
  "nom": "Diop",
  "prenom": "Amina"
}
```

### 2. **Virgule en Trop**
❌ INCORRECT:
```json
{
  "nom": "Diop",
  "prenom": "Amina",
}
```

✅ CORRECT:
```json
{
  "nom": "Diop",
  "prenom": "Amina"
}
```

### 3. **Guillemets Simples**
❌ INCORRECT:
```json
{
  'nom': 'Diop'
}
```

✅ CORRECT:
```json
{
  "nom": "Diop"
}
```

### 4. **Commentaires Interdits**
❌ INCORRECT:
```json
{
  "nom": "Diop", // Nom de famille
  "prenom": "Amina" /* Prénom */
}
```

✅ CORRECT:
```json
{
  "nom": "Diop",
  "prenom": "Amina"
}
```

---

## 🧪 **Testez Votre JSON**

### Copiez-collez ce JSON valide:
```json
{
  "nom": "Test",
  "prenom": "Utilisateur",
  "telephone": "770000000",
  "email": "test@example.com",
  "localisation": "Test Ville",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "test123"
}
```

---

## 🚀 **Endpoint de Test**

```http
POST http://localhost:8080/suguconnect/consommateur/inscription
Content-Type: application/json

{
  "nom": "Test",
  "prenom": "Utilisateur",
  "telephone": "770000000",
  "email": "test@example.com",
  "localisation": "Test Ville",
  "latitude": 0,
  "longitude": 0,
  "motDePasse": "test123"
}
```

**Réponse attendue:**
```json
"Soyez le bienvenue "
```

---

## 📞 **En Cas de Problème**

1. **Validez votre JSON** sur https://jsonlint.com/
2. **Vérifiez les logs** de l'application
3. **Comparez avec l'exemple ci-dessus**
4. **Assurez-vous que tous les champs sont présents**

---

**Ce validateur est conçu pour résoudre 99% des erreurs JSON dans SuguConnect**