# 🚀 Test Rapide du Système d'Upload

## Démarrage

```bash
mvn spring-boot:run
```

Ouvrir Swagger : http://localhost:8080/suguconnect/swagger-ui.html

---

## 1️⃣ S'Authentifier

### Login Admin
```
POST /auth/login/admin
Body:
{
  "telephone": "70000000",
  "motDePasse": "admin123"
}
```

**Copier le token JWT de la réponse**

### Autoriser dans Swagger
1. Cliquer sur "Authorize" (🔓)
2. Coller le token
3. Cliquer "Authorize" puis "Close"

---

## 2️⃣ Tester Upload Simple

### Upload 1 Fichier
```
POST /files/upload
- Sélectionner un fichier (image JPG/PNG)
- Execute
```

**Réponse attendue :**
```json
{
  "fileName": "uuid.jpg",
  "fileDownloadUri": "http://localhost:8080/suguconnect/files/download/uuid.jpg",
  "fileType": "image/jpeg",
  "size": "123456"
}
```

### Télécharger le Fichier
```
GET /files/download/uuid.jpg
```
Copier l'URL de `fileDownloadUri` et l'ouvrir dans le navigateur → L'image doit s'afficher

---

## 3️⃣ Créer un Producteur Actif

### S'inscrire en tant que Producteur
```
POST /producteur/inscription
Body:
{
  "nom": "Diallo",
  "prenom": "Amadou",
  "telephone": "77123456",
  "email": "amadou@test.com",
  "motDePasse": "password123",
  "localisation": "Conakry",
  "description": "Producteur de fruits"
}
```

### Valider le Producteur (En tant qu'Admin)
```
PUT /admin/producteur/validation/{id}
RequestParam: decision=ACCEPTE
```
*Remplacer {id} par l'ID du producteur créé*

---

## 4️⃣ Ajouter un Produit avec Photos

### Login Producteur
```
POST /auth/login/producteur
Body:
{
  "telephone": "77123456",
  "motDePasse": "password123"
}
```
**Copier le nouveau token et re-autoriser dans Swagger**

### Créer Catégorie (si nécessaire - en tant qu'Admin)
```
POST /categorie
Body:
{
  "libelle": "Fruits",
  "description": "Fruits frais"
}
```

### Ajouter Produit avec Photos
```
POST /producteur/{producteurId}/produit
Content-Type: multipart/form-data

Paramètres:
- producteurId: [ID du producteur]
- nom: Mangues Bio
- description: Mangues fraîches de notre ferme
- prixUnitaire: 2500
- unite: KILOGRAMME
- quantite: 100
- categorieId: 1
- photos: [Sélectionner 2-3 images]
```

**Réponse attendue :**
```
Produit ajouté avec succès avec 3 photos, ID: 15
```

---

## 5️⃣ Vérifier les Photos

### Lister les Produits
```
GET /producteur/{producteurId}/produit
```

**Réponse :**
```json
[
  {
    "id": 15,
    "nom": "Mangues Bio",
    "photos": [
      "http://localhost:8080/suguconnect/files/download/uuid1.jpg",
      "http://localhost:8080/suguconnect/files/download/uuid2.jpg",
      "http://localhost:8080/suguconnect/files/download/uuid3.jpg"
    ]
  }
]
```

### Ouvrir les URLs des photos dans le navigateur
Toutes les images doivent s'afficher correctement ✅

---

## 6️⃣ Tests Supplémentaires

### Upload Multiple
```
POST /files/upload-multiple
- Sélectionner 3-4 fichiers
- Execute
```

### Modifier un Produit
```
PUT /producteur/{producteurId}/produit/{produitId}
- nom: Mangues Bio Premium
- prixUnitaire: 2800
- photos: [Nouvelles images]
```

### Supprimer un Fichier
```
DELETE /files/delete/uuid.jpg
```

---

## 📋 Checklist de Test

- [ ] Upload d'un fichier simple
- [ ] Download du fichier (affichage dans navigateur)
- [ ] Upload multiple (3 fichiers)
- [ ] Création producteur + validation
- [ ] Ajout produit avec 2-3 photos
- [ ] Vérification des URLs de photos
- [ ] Modification produit avec nouvelles photos
- [ ] Suppression d'un fichier

---

## ⚠️ Erreurs Communes

### "Maximum upload size exceeded"
- Fichier trop grand (> 10 MB)
- **Solution :** Utiliser une image plus petite

### "Au moins une photo est requise"
- Aucune photo sélectionnée
- **Solution :** Sélectionner au moins 1 photo

### "Maximum 4 photos autorisées"
- Plus de 4 photos sélectionnées
- **Solution :** Réduire à 4 photos maximum

### 403 Forbidden
- Token JWT expiré ou manquant
- **Solution :** Se reconnecter et obtenir un nouveau token

### "Vous n'avez pas de droit pour ajouter un produit"
- Producteur pas encore validé (statut EN_ATTENTE)
- **Solution :** Valider le producteur via endpoint admin

---

## 📂 Vérification Système de Fichiers

### Emplacement des fichiers
```
SuguConnect/
  uploads/
    a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg
    b4c6d8e0-f2g4-5678-90bc-def012345678.png
    ...
```

Le dossier `uploads/` est créé automatiquement au démarrage de l'application.

---

## ✅ Résultat Attendu

Après tous les tests :
1. ✅ Fichiers uploadés dans `/uploads`
2. ✅ Images accessibles via URL publique
3. ✅ Produits avec 1-4 photos
4. ✅ Photos affichables dans navigateur
5. ✅ Modification/Suppression fonctionnent

**Système d'upload 100% opérationnel ! 🎉**
