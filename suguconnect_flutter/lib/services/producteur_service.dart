import '../config/api_config.dart';
import 'api_service.dart';
import 'dart:io';

class ProducteurService {
  final ApiService _apiService;

  ProducteurService(this._apiService);

  // Inscription
  Future<String> inscription(Map<String, dynamic> data) async {
    try {
      final response = await _apiService.post<String>(
        '${ApiConfig.producteur}/inscription',
        data: data,
      );
      return response.data ?? 'Inscription réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer tous les producteurs
  Future<List<dynamic>> getProducteurs() async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.producteur}/producteurs',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer un producteur par ID
  Future<Map<String, dynamic>> getProducteur(int id) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.producteur}/$id',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Modifier un producteur
  Future<String> modifierProducteur(int id, Map<String, dynamic> data) async {
    try {
      final response = await _apiService.put<String>(
        '${ApiConfig.producteur}/$id',
        data: data,
      );
      return response.data ?? 'Modification réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Supprimer un producteur
  Future<String> supprimerProducteur(int id) async {
    try {
      final response = await _apiService.delete<String>(
        '${ApiConfig.producteur}/$id',
      );
      return response.data ?? 'Suppression réussie';
    } catch (e) {
      rethrow;
    }
  }

  // Ajouter un produit avec photos
  Future<String> ajouterProduit(
    int producteurId,
    Map<String, dynamic> produitData,
    List<File> photos,
  ) async {
    try {
      final formData = {
        'nom': produitData['nom'],
        'description': produitData['description'] ?? '',
        'prixUnitaire': produitData['prixUnitaire'].toString(),
        'unite': produitData['unite'],
        'quantite': produitData['quantite'].toString(),
        'categorieId': produitData['categorieId'].toString(),
      };

      final photoPaths = photos.map((file) => file.path).toList();
      final response = await _apiService.uploadMultipleFiles<String>(
        '${ApiConfig.producteur}/$producteurId/produit',
        photoPaths,
        fieldName: 'photos',
        additionalData: formData,
      );

      return response.data ?? 'Produit ajouté avec succès';
    } catch (e) {
      rethrow;
    }
  }

  // Lister les produits d'un producteur
  Future<List<dynamic>> listerProduits(int producteurId) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.producteur}/$producteurId/produit',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Rechercher des produits
  Future<List<dynamic>> rechercherProduits(int producteurId, String nom) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.producteur}/$producteurId/produit/recherche',
        queryParameters: {'nom': nom},
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Historique des ventes
  Future<List<dynamic>> getHistoriqueVentes(int producteurId) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.producteur}/$producteurId/ventes',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Modifier un produit
  Future<String> modifierProduit(
    int producteurId,
    int produitId,
    Map<String, dynamic>? produitData,
    List<File>? photos,
  ) async {
    try {
      final formData = <String, dynamic>{};
      
      if (produitData != null) {
        if (produitData['nom'] != null) formData['nom'] = produitData['nom'];
        if (produitData['description'] != null) formData['description'] = produitData['description'];
        if (produitData['prixUnitaire'] != null) formData['prixUnitaire'] = produitData['prixUnitaire'].toString();
        if (produitData['unite'] != null) formData['unite'] = produitData['unite'];
        if (produitData['quantite'] != null) formData['quantite'] = produitData['quantite'].toString();
      }

      if (photos != null && photos.isNotEmpty) {
        final photoPaths = photos.map((file) => file.path).toList();
        final response = await _apiService.uploadMultipleFiles<String>(
          '${ApiConfig.producteur}/$producteurId/produit/$produitId',
          photoPaths,
          fieldName: 'photos',
          additionalData: formData,
        );
        return response.data ?? 'Produit modifié avec succès';
      } else {
        final response = await _apiService.put<String>(
          '${ApiConfig.producteur}/$producteurId/produit/$produitId',
          data: formData,
        );
        return response.data ?? 'Produit modifié avec succès';
      }
    } catch (e) {
      rethrow;
    }
  }

  // Supprimer un produit
  Future<String> supprimerProduit(int producteurId, int produitId) async {
    try {
      final response = await _apiService.delete<String>(
        '${ApiConfig.producteur}/$producteurId/produit/$produitId',
      );
      return response.data ?? 'Produit supprimé avec succès';
    } catch (e) {
      rethrow;
    }
  }

  // Changer le statut d'une commande
  Future<Map<String, dynamic>> changerStatutCommande(
    int commandeId,
    int producteurId,
    String nouveauStatut,
    String? motifRejet,
  ) async {
    try {
      final queryParams = {
        'producteurId': producteurId,
        'nouveauStatut': nouveauStatut,
        if (motifRejet != null) 'motifRejet': motifRejet,
      };

      final response = await _apiService.put<Map<String, dynamic>>(
        '${ApiConfig.producteur}/commande/$commandeId/statut',
        queryParameters: queryParams,
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }
}


