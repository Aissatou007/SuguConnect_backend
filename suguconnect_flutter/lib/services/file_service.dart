import '../config/api_config.dart';
import 'api_service.dart';
import 'dart:io';

class FileService {
  final ApiService _apiService;

  FileService(this._apiService);

  // Upload un fichier unique
  Future<Map<String, dynamic>> uploadFile(File file) async {
    try {
      final response = await _apiService.uploadFile<Map<String, dynamic>>(
        '${ApiConfig.files}/upload',
        file.path,
        fieldName: 'file',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Upload plusieurs fichiers
  Future<List<dynamic>> uploadMultipleFiles(List<File> files) async {
    try {
      final filePaths = files.map((file) => file.path).toList();
      final response = await _apiService.uploadMultipleFiles<List<dynamic>>(
        '${ApiConfig.files}/upload-multiple',
        filePaths,
        fieldName: 'files',
      );
      return response.data ?? [];
    } catch (e) {
      rethrow;
    }
  }

  // Télécharger un fichier
  Future<void> downloadFile(String fileName, String savePath) async {
    try {
      await _apiService.downloadFile(
        '${ApiConfig.files}/download/$fileName',
        savePath,
      );
    } catch (e) {
      rethrow;
    }
  }

  // Supprimer un fichier
  Future<String> deleteFile(String fileName) async {
    try {
      final response = await _apiService.delete<Map<String, dynamic>>(
        '${ApiConfig.files}/delete/$fileName',
      );
      return response.data?['message'] ?? 'Fichier supprimé avec succès';
    } catch (e) {
      rethrow;
    }
  }

  // Upload les photos d'un produit
  Future<Map<String, dynamic>> uploadProductPhotos(int productId, List<File> photos) async {
    try {
      final photoPaths = photos.map((file) => file.path).toList();
      final response = await _apiService.uploadMultipleFiles<Map<String, dynamic>>(
        '${ApiConfig.files}/product/$productId/upload-photos',
        photoPaths,
        fieldName: 'photos',
      );
      return response.data ?? {};
    } catch (e) {
      rethrow;
    }
  }

  // Obtenir l'URL de téléchargement
  String getDownloadUrl(String fileName) {
    return '${ApiConfig.baseUrl}${ApiConfig.files}/download/$fileName';
  }
}


