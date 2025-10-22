# Guide d'Utilisation du Système d'Upload de Fichiers

## 📁 Configuration

Le système d'upload de fichiers a été complètement intégré dans votre backend SuguConnect.

### Configuration dans `application.properties`

```properties
# Configuration du stockage de fichiers
file.upload-dir=./uploads
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
```

**Explication :**
- `file.upload-dir` : Répertoire où les fichiers seront stockés (créé automatiquement)
- `max-file-size` : Taille maximale d'un fichier unique (10 MB)
- `max-request-size` : Taille maximale d'une requête (50 MB pour plusieurs fichiers)

---

## 🚀 Endpoints Disponibles

### 1. Upload d'un Fichier Unique
**Endpoint :** `POST /suguconnect/files/upload`  
**Authentification :** Requise  
**Content-Type :** `multipart/form-data`

**Paramètres :**
- `file` (MultipartFile) : Le fichier à uploader

**Exemple avec Postman/Bruno :**
```
POST http://localhost:8080/suguconnect/files/upload
Headers:
  Authorization: Bearer {votre_token_jwt}
  Content-Type: multipart/form-data

Body (form-data):
  file: [Sélectionner un fichier]
```

**Réponse :**
```json
{
  "fileName": "a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg",
  "fileDownloadUri": "http://localhost:8080/suguconnect/files/download/a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg",
  "fileType": "image/jpeg",
  "size": "245678"
}
```

---

### 2. Upload de Plusieurs Fichiers
**Endpoint :** `POST /suguconnect/files/upload-multiple`  
**Authentification :** Requise  
**Content-Type :** `multipart/form-data`

**Paramètres :**
- `files` (MultipartFile[]) : Tableau de fichiers

**Exemple :**
```
POST http://localhost:8080/suguconnect/files/upload-multiple
Headers:
  Authorization: Bearer {votre_token_jwt}
  Content-Type: multipart/form-data

Body (form-data):
  files: [Fichier 1]
  files: [Fichier 2]
  files: [Fichier 3]
```

**Réponse :**
```json
[
  {
    "fileName": "uuid1.jpg",
    "fileDownloadUri": "http://localhost:8080/suguconnect/files/download/uuid1.jpg",
    "fileType": "image/jpeg",
    "size": "123456"
  },
  {
    "fileName": "uuid2.png",
    "fileDownloadUri": "http://localhost:8080/suguconnect/files/download/uuid2.png",
    "fileType": "image/png",
    "size": "234567"
  }
]
```

---

### 3. Télécharger un Fichier
**Endpoint :** `GET /suguconnect/files/download/{fileName}`  
**Authentification :** Non requise (Public)

**Exemple :**
```
GET http://localhost:8080/suguconnect/files/download/a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg
```

Le fichier sera retourné directement (affichable dans le navigateur pour les images).

---

### 4. Supprimer un Fichier
**Endpoint :** `DELETE /suguconnect/files/delete/{fileName}`  
**Authentification :** Requise

**Exemple :**
```
DELETE http://localhost:8080/suguconnect/files/delete/a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg
Headers:
  Authorization: Bearer {votre_token_jwt}
```

**Réponse :**
```json
{
  "message": "Fichier supprimé avec succès",
  "fileName": "a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg"
}
```

---

### 5. Upload des Photos d'un Produit
**Endpoint :** `POST /suguconnect/files/product/{productId}/upload-photos`  
**Authentification :** Requise  
**Content-Type :** `multipart/form-data`

**Paramètres :**
- `productId` (int) : ID du produit
- `photos` (MultipartFile[]) : Photos du produit (1 à 4)

**Réponse :**
```json
{
  "productId": 5,
  "photoUrls": [
    "http://localhost:8080/suguconnect/files/download/uuid1.jpg",
    "http://localhost:8080/suguconnect/files/download/uuid2.jpg"
  ],
  "totalPhotos": 2
}
```

---

## 📦 Ajouter un Produit avec Photos

### Endpoint Mis à Jour
**Endpoint :** `POST /suguconnect/producteur/{producteurId}/produit`  
**Authentification :** Requise (PRODUCTEUR ou ADMIN)  
**Content-Type :** `multipart/form-data`

**Paramètres :**
- `nom` (String) : Nom du produit (requis)
- `description` (String) : Description (optionnel)
- `prixUnitaire` (float) : Prix unitaire (requis)
- `unite` (String) : Unité - KILOGRAMME, GRAMME, LITRE, PIECE, PAQUET (requis)
- `quantite` (int) : Quantité initiale (requis)
- `categorieId` (int) : ID de la catégorie (requis)
- `photos` (MultipartFile[]) : Photos du produit (1 à 4 photos requises)

**Exemple avec Swagger UI :**
1. Cliquez sur l'endpoint `POST /producteur/{producteurId}/produit`
2. Cliquez sur "Try it out"
3. Remplissez :
   - `producteurId` : 5
   - `nom` : Mangues Bio
   - `description` : Mangues fraîches de notre ferme
   - `prixUnitaire` : 2500
   - `unite` : KILOGRAMME
   - `quantite` : 100
   - `categorieId` : 1
   - `photos` : [Sélectionnez 1-4 images]
4. Cliquez sur "Execute"

**Exemple avec Postman/Bruno :**
```
POST http://localhost:8080/suguconnect/producteur/5/produit
Headers:
  Authorization: Bearer {votre_token_jwt}
  Content-Type: multipart/form-data

Body (form-data):
  nom: Mangues Bio
  description: Mangues fraîches biologiques
  prixUnitaire: 2500
  unite: KILOGRAMME
  quantite: 100
  categorieId: 1
  photos: [Image 1]
  photos: [Image 2]
```

**Réponse :**
```
Produit ajouté avec succès avec 2 photos, ID: 15
```

---

## 🔄 Modifier un Produit avec Photos

**Endpoint :** `PUT /suguconnect/producteur/{producteurId}/produit/{produitId}`  
**Content-Type :** `multipart/form-data`

**Paramètres (tous optionnels) :**
- `nom` (String)
- `description` (String)
- `prixUnitaire` (float)
- `unite` (String)
- `quantite` (int)
- `photos` (MultipartFile[]) : Nouvelles photos (remplace les anciennes)

**Exemple :**
```
PUT http://localhost:8080/suguconnect/producteur/5/produit/15
Headers:
  Authorization: Bearer {votre_token_jwt}
  Content-Type: multipart/form-data

Body (form-data):
  nom: Mangues Bio Premium
  prixUnitaire: 2800
  photos: [Nouvelle Image 1]
  photos: [Nouvelle Image 2]
```

**Note :** Si vous ne fournissez pas de photos, les anciennes photos sont conservées.

---

## 🔒 Configuration de Sécurité

### Endpoints Publics (Pas d'authentification) :
- `GET /files/download/{fileName}` - Télécharger un fichier

### Endpoints Authentifiés :
- `POST /files/upload` - Upload un fichier
- `POST /files/upload-multiple` - Upload plusieurs fichiers
- `DELETE /files/delete/{fileName}` - Supprimer un fichier
- `POST /files/product/{productId}/upload-photos` - Upload photos produit

### Configuration dans SecurityConfig :
```java
// Téléchargement public (pour afficher les images)
.requestMatchers("/files/download/**").permitAll()

// Upload et suppression nécessitent authentification
.requestMatchers("/files/upload", "/files/upload-multiple").authenticated()
.requestMatchers("/files/delete/**").authenticated()
.requestMatchers("/files/product/*/upload-photos").authenticated()
```

---

## 📂 Structure des Fichiers

### Répertoire de Stockage
Les fichiers sont stockés dans : `./uploads/` (à la racine du projet)

### Nommage des Fichiers
Chaque fichier uploadé reçoit un nom unique généré avec UUID :
```
Original: image.jpg
Stocké: a3b5c7d9-e1f3-4567-89ab-cdef01234567.jpg
```

**Avantages :**
- Évite les conflits de noms
- Sécurise contre les attaques par nom de fichier
- Conserve l'extension d'origine

---

## 🛠️ Service FileStorageService

### Méthodes Disponibles

#### 1. `storeFile(MultipartFile file)`
Stocke un fichier unique et retourne son nom.

```java
String fileName = fileStorageService.storeFile(file);
```

#### 2. `storeFiles(MultipartFile[] files)`
Stocke plusieurs fichiers et retourne la liste des noms.

```java
List<String> fileNames = fileStorageService.storeFiles(files);
```

#### 3. `loadFileAsResource(String fileName)`
Charge un fichier en tant que Resource pour le téléchargement.

```java
Resource resource = fileStorageService.loadFileAsResource(fileName);
```

#### 4. `deleteFile(String fileName)`
Supprime un fichier.

```java
fileStorageService.deleteFile(fileName);
```

#### 5. `deleteFiles(List<String> fileNames)`
Supprime plusieurs fichiers.

```java
fileStorageService.deleteFiles(fileNames);
```

---

## 📝 Exemple d'Intégration Frontend

### Upload d'un Fichier avec JavaScript

```javascript
async function uploadFile(file, token) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch('http://localhost:8080/suguconnect/files/upload', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: formData
    });

    const result = await response.json();
    console.log('Fichier uploadé:', result.fileDownloadUri);
    return result;
}
```

### Ajouter un Produit avec Photos

```javascript
async function ajouterProduit(producteurId, produitData, photos, token) {
    const formData = new FormData();
    
    // Ajouter les données du produit
    formData.append('nom', produitData.nom);
    formData.append('description', produitData.description);
    formData.append('prixUnitaire', produitData.prixUnitaire);
    formData.append('unite', produitData.unite);
    formData.append('quantite', produitData.quantite);
    formData.append('categorieId', produitData.categorieId);
    
    // Ajouter les photos
    photos.forEach(photo => {
        formData.append('photos', photo);
    });

    const response = await fetch(
        `http://localhost:8080/suguconnect/producteur/${producteurId}/produit`,
        {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        }
    );

    return await response.text();
}
```

### Afficher une Image

```html
<img src="http://localhost:8080/suguconnect/files/download/uuid.jpg" 
     alt="Photo du produit" />
```

---

## ✅ Validation et Contraintes

### Contraintes du Système

1. **Taille des Fichiers :**
   - Fichier unique : Max 10 MB
   - Requête totale : Max 50 MB

2. **Photos des Produits :**
   - Minimum : 1 photo obligatoire
   - Maximum : 4 photos par produit

3. **Sécurité :**
   - Noms de fichiers nettoyés (pas de `..` ou chemins relatifs)
   - Noms UUID pour éviter les conflits
   - Téléchargement public, upload/suppression authentifié

### Messages d'Erreur

| Erreur | Message |
|--------|---------|
| Aucune photo | "Au moins une photo est requise" |
| Trop de photos | "Maximum 4 photos autorisées" |
| Fichier trop grand | "Maximum upload size exceeded" |
| Fichier invalide | "Le nom du fichier contient une séquence de chemin invalide" |

---

## 🧪 Tests avec Swagger UI

1. **Accéder à Swagger :**
   ```
   http://localhost:8080/suguconnect/swagger-ui.html
   ```

2. **S'authentifier :**
   - Cliquez sur "Authorize" (bouton cadenas)
   - Entrez votre token JWT
   - Cliquez sur "Authorize" puis "Close"

3. **Tester l'upload :**
   - Allez dans "Gestion des Fichiers"
   - Choisissez `POST /files/upload`
   - Cliquez "Try it out"
   - Sélectionnez un fichier
   - Cliquez "Execute"

4. **Tester l'ajout de produit :**
   - Allez dans "Producteur"
   - Choisissez `POST /producteur/{producteurId}/produit`
   - Remplissez tous les champs
   - Sélectionnez 1-4 photos
   - Cliquez "Execute"

---

## 🔍 Dépannage

### Erreur : "Impossible de créer le répertoire de stockage"
**Solution :** Vérifiez les permissions du dossier ou créez manuellement le dossier `./uploads`

### Erreur : "Maximum upload size exceeded"
**Solution :** Réduisez la taille des fichiers ou modifiez les limites dans `application.properties`

### Erreur : "Fichier non trouvé"
**Solution :** Vérifiez que le nom du fichier est correct et que le fichier existe dans `/uploads`

### Les images ne s'affichent pas
**Solution :** Vérifiez que l'endpoint `/files/download/**` est accessible publiquement dans SecurityConfig

---

## 📊 Résumé des Changements

### Fichiers Créés
1. ✅ `FileStorageProperties.java` - Configuration des uploads
2. ✅ `FileStorageService.java` - Service de gestion des fichiers
3. ✅ `FileUploadController.java` - API d'upload/download
4. ✅ `ProduitRequestDTO.java` - DTO pour produit avec photos

### Fichiers Modifiés
1. ✅ `application.properties` - Ajout config multipart
2. ✅ `SecurityConfig.java` - Endpoints publics/protégés
3. ✅ `ProducteurController.java` - Support multipart pour produits
4. ✅ `ProduitService.java` - Gestion photos et catégorie

---

## 🎯 Prochaines Étapes

1. Démarrer l'application
2. Tester l'upload dans Swagger UI
3. Ajouter un produit avec photos
4. Vérifier que les images s'affichent
5. Intégrer avec votre frontend

**Le système est maintenant prêt à l'emploi ! 🚀**
