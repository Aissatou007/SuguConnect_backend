import '../config/api_config.dart';
import 'api_service.dart';
import 'dart:io';

class CategorieService {
  final ApiService _apiService;

  CategorieService(this._apiService);

  // Créer une catégorie
  Future<Map<String, dynamic>> creerCategorie(String libelle, File? photo) async {
    try {
      if (photo != null) {
        final response = await _apiService.uploadFile<Map<String, dynamic>>(
          ApiConfig.categorie,
          photo.path,
          fieldName: 'photo',
          additionalData: {'libelle': libelle},
        );
        return response.data ?? {};
      } else {
        final response = await _apiService.post<Map<String, dynamic>>(
          ApiConfig.categorie,
          queryParameters: {'libelle': libelle},
        );
        return response.data ?? {};
      }
    } catch (e) {
      rethrow;
    }
  }

  // Modifier une catégorie
  Future<Map<String, dynamic>> modifierCategorie(int id, Map<String, dynamic> data) async {
    try {
      final response = await _apiService.put<Map<String, dynamic>>(
        '${ApiConfig.categorie}/$id',
        data: data,
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Supprimer une catégorie
  Future<String> supprimerCategorie(int id) async {
    try {
      final response = await _apiService.delete<String>(
        '${ApiConfig.categorie}/$id',
      );
      return response.data ?? 'Catégorie supprimée avec succès';
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer toutes les catégories
  Future<List<dynamic>> getCategories() async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        ApiConfig.categorie,
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer une catégorie par ID
  Future<Map<String, dynamic>> getCategorie(int id) async {
    try {
      final response = await _apiService.get<Map<String, dynamic>>(
        '${ApiConfig.categorie}/$id',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Récupérer les produits d'une catégorie
  Future<List<dynamic>> getProduitsParCategorie(int id) async {
    try {
      final response = await _apiService.get<List<dynamic>>(
        '${ApiConfig.categorie}/$id/produits',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }
}


