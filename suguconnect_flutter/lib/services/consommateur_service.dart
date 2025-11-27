import '../config/api_config.dart';
import 'api_service.dart';
import 'dart:io';

class ConsommateurService {
  final ApiService _apiService;

  ConsommateurService(this._apiService);

  // Inscription
  Future<String> inscription(Map<String, dynamic> data) async {
    try {
      final response = await _apiService.post<String>(
        '${ApiConfig.consommateur}/inscription',
        data: data,
      );
      return response.data ?? 'Inscription réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer tous les consommateurs
  Future<List<dynamic>> getConsommateurs() async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.consommateur}/consommateurs',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer un consommateur par ID
  Future<Map<String, dynamic>> getConsommateur(int id) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.consommateur}/$id',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Modifier un consommateur
  Future<String> modifierConsommateur(int id, Map<String, dynamic> data) async {
    try {
      final response = await _apiService.put<String>(
        '${ApiConfig.consommateur}/$id',
        data: data,
      );
      return response.data ?? 'Modification réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Supprimer un consommateur
  Future<String> supprimerConsommateur(int id) async {
    try {
      final response = await _apiService.delete<String>(
        '${ApiConfig.consommateur}/$id',
      );
      return response.data ?? 'Suppression réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Voir les produits disponibles
  Future<List<dynamic>> getProduits() async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.consommateur}/produits',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Rechercher des produits
  Future<List<dynamic>> rechercherProduits(String nom) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.consommateur}/produits/recherche',
        queryParameters: {'nom': nom},
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer les produits disponibles d'une catégorie
  Future<List<dynamic>> getProduitsParCategorie(int categorieId) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.consommateur}/produits/categorie/$categorieId',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Ajouter au panier
  Future<String> ajouterAuPanier(int idConsommateur, int idProduit, int quantite) async {
    try {
      final response = await _apiService.post<String>(
        '${ApiConfig.consommateur}/$idConsommateur/panier/ajouter/$idProduit',
        queryParameters: {'quantite': quantite},
      );
      return response.data ?? 'Produit ajouté au panier';
    } catch (e) {
      rethrow;
    }
  }

  // Retirer du panier
  Future<String> retirerDuPanier(int idConsommateur, int idProduit) async {
    try {
      final response = await _apiService.delete<String>(
        '${ApiConfig.consommateur}/$idConsommateur/panier/retirer/$idProduit',
      );
      return response.data ?? 'Produit retiré du panier';
    } catch (e) {
      rethrow;
    }
  }

  // Voir le panier
  Future<Map<String, dynamic>> voirPanier(int idConsommateur) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.consommateur}/$idConsommateur/panier',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Voir les commandes
  Future<List<dynamic>> voirCommandes(int idConsommateur) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.consommateur}/$idConsommateur/commandes',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Passer une commande
  Future<Map<String, dynamic>> passerCommande(
    int idConsommateur,
    Map<String, dynamic> data,
  ) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.consommateur}/$idConsommateur/commande',
        data: data,
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Voir une commande spécifique
  Future<Map<String, dynamic>> voirCommande(int commandeId) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.consommateur}/commande/$commandeId',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Valider la réception
  Future<Map<String, dynamic>> validerReception(int commandeId, int consommateurId) async {
    try {
      final response = await _apiService.post<Map<String, dynamic>>(
        '${ApiConfig.consommateur}/commande/$commandeId/valider-reception',
        queryParameters: {'consommateurId': consommateurId},
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }
}

