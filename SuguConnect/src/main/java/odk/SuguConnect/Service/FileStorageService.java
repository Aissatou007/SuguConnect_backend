package odk.SuguConnect.Service;

import odk.SuguConnect.Config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final ImageService imageService;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, ImageService imageService) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.imageService = imageService;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible de créer le répertoire de stockage des fichiers.", ex);
        }
    }

    /**
     * Stocke un fichier unique
     */
    public String storeFile(MultipartFile file) {
        return storeFileWithThumbnail(file, 300, 300); // Valeurs par défaut pour les vignettes
    }

    /**
     * Stocke un fichier unique avec génération de vignettes
     */
    public String storeFileWithThumbnail(MultipartFile file, int thumbnailWidth, int thumbnailHeight) {
        // Nettoyer le nom du fichier
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Vérifier si le fichier contient des caractères invalides
            if(originalFileName.contains("..")) {
                throw new RuntimeException("Le nom du fichier contient une séquence de chemin invalide " + originalFileName);
            }

            // Générer un nom de fichier unique
            String fileExtension = "";
            if(originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Copier le fichier vers l'emplacement cible
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Générer et stocker les vignettes si le fichier est une image
            if (isImageFile(fileExtension)) {
                generateAndStoreThumbnails(file, newFileName, thumbnailWidth, thumbnailHeight);
            }

            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de stocker le fichier " + originalFileName + ". Veuillez réessayer!", ex);
        }
    }

    /**
     * Stocke plusieurs fichiers
     */
    public List<String> storeFiles(MultipartFile[] files) {
        List<String> fileNames = new ArrayList<>();
        
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = storeFile(file);
                    fileNames.add(fileName);
                }
            }
        }
        
        return fileNames;
    }

    /**
     * Charge un fichier en tant que Resource
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier non trouvé " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Fichier non trouvé " + fileName, ex);
        }
    }

    /**
     * Supprime un fichier
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de supprimer le fichier " + fileName, ex);
        }
    }

    /**
     * Supprime plusieurs fichiers
     */
    public void deleteFiles(List<String> fileNames) {
        if (fileNames != null && !fileNames.isEmpty()) {
            for (String fileName : fileNames) {
                deleteFile(fileName);
            }
        }
    }

    /**
     * Vérifie si un fichier est une image
     */
    private boolean isImageFile(String fileExtension) {
        return fileExtension != null && 
               (fileExtension.equalsIgnoreCase(".jpg") || 
                fileExtension.equalsIgnoreCase(".jpeg") || 
                fileExtension.equalsIgnoreCase(".png") || 
                fileExtension.equalsIgnoreCase(".gif") || 
                fileExtension.equalsIgnoreCase(".bmp"));
    }

    /**
     * Génère et stocke les vignettes pour une image
     */
    private void generateAndStoreThumbnails(MultipartFile originalFile, String originalFileName, int width, int height) {
        try {
            // Générer la vignette en utilisant le nouveau ImageService
            byte[] thumbnailBytes = imageService.createThumbnail(originalFile, width, height);
            
            // Sauvegarder la vignette
            String thumbnailFileName = "thumb_" + originalFileName;
            Path thumbnailPath = this.fileStorageLocation.resolve(thumbnailFileName);
            Files.write(thumbnailPath, thumbnailBytes);
        } catch (Exception e) {
            // En cas d'erreur, on continue sans vignette
            System.err.println("Impossible de générer la vignette pour " + originalFileName + ": " + e.getMessage());
        }
    }

    /**
     * Obtient le chemin du répertoire de stockage
     */
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}