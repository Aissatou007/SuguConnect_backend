# Fonctionnalité de Chat - SuguConnect

## Description
Cette fonctionnalité permet aux consommateurs d'envoyer des messages aux producteurs via l'application mobile. Les utilisateurs peuvent échanger des messages texte, images, fichiers vocaux et documents.

## Architecture

### Entités
1. **Message** - Représente un message échangé entre un consommateur et un producteur
2. **Conversation** - Regroupe les messages entre deux utilisateurs concernant un produit spécifique

### Enums
- **TypeMessage** - Définit le type de message (TEXTE, IMAGE, VOCAL, DOCUMENT)

### Services
1. **MessageService** - Gère la logique métier des messages
2. **ConversationService** - Gère les conversations entre utilisateurs
3. **NotificationChatService** - Envoie des notifications lors de nouveaux messages

### Contrôleurs
- **ChatController** - Expose les endpoints REST pour l'API de chat

## Endpoints API

### Conversations
- `POST /api/chat/conversation` - Créer ou récupérer une conversation
- `GET /api/chat/conversations/consommateur/{consommateurId}` - Récupérer les conversations d'un consommateur
- `GET /api/chat/conversations/producteur/{producteurId}` - Récupérer les conversations d'un producteur

### Messages
- `POST /api/chat/message/texte` - Envoyer un message texte
- `POST /api/chat/message/fichier` - Envoyer un message avec fichier
- `GET /api/chat/messages/{conversationId}` - Récupérer les messages d'une conversation
- `PUT /api/chat/message/{messageId}/lu` - Marquer un message comme lu

### Fichiers
- `POST /api/chat/upload/fichier` - Uploader un fichier pour le chat

## Intégration Mobile

### Interface utilisateur
L'interface mobile comprend :
1. **Liste des conversations** - Affiche toutes les discussions en cours
2. **Écran de chat** - Interface de discussion individuelle avec un producteur
3. **Options d'envoi** - Boutons pour envoyer texte, images, vocaux et documents

### Fonctionnalités
- Envoi de messages texte en temps réel
- Envoi d'images depuis la galerie ou l'appareil photo
- Enregistrement et envoi de messages vocaux
- Envoi de documents
- Notification de nouveaux messages
- Marquage des messages comme lus

## Sécurité
- Tous les échanges sont associés aux utilisateurs authentifiés
- Les fichiers sont stockés de manière sécurisée sur le serveur
- Les conversations sont privées entre les participants

## Utilisation

### Création d'une conversation
Lorsqu'un consommateur clique sur "Discuter" dans les détails d'un produit, une conversation est automatiquement créée entre lui et le producteur.

### Envoi de messages
1. **Texte** : Saisir le message dans le champ de texte et appuyer sur Envoyer
2. **Images** : Cliquer sur l'icône pièce jointe et sélectionner une image
3. **Vocaux** : Maintenir le bouton micro pour enregistrer, relâcher pour envoyer
4. **Documents** : Cliquer sur l'icône pièce jointe et sélectionner un document

## Maintenance
- Les messages et conversations sont stockés dans la base de données
- Les fichiers uploadés sont stockés dans le dossier `/uploads`
- Les notifications sont envoyées via le système de notification existant