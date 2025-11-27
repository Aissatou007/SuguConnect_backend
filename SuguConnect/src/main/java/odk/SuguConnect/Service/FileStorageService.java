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

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        String uploadDir = fileStorageProperties.getUploadDir();
        
        // Si le chemin est relatif, le rendre absolu depuis le répertoire de travail
        if (!Paths.get(uploadDir).isAbsolute()) {
            // Utiliser le répertoire de travail de l'application
            String workingDir = System.getProperty("user.dir");
            this.fileStorageLocation = Paths.get(workingDir, uploadDir).toAbsolutePath().normalize();
        } else {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        }

        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("==========================================");
            System.out.println("CONFIGURATION DU STOCKAGE DE FICHIERS");
            System.out.println("Chemin configuré: " + uploadDir);
            System.out.println("Répertoire de travail: " + System.getProperty("user.dir"));
            System.out.println("Répertoire de stockage ABSOLU: " + this.fileStorageLocation.toAbsolutePath());
            System.out.println("Répertoire existe: " + Files.exists(this.fileStorageLocation));
            System.out.println("Répertoire est accessible en écriture: " + Files.isWritable(this.fileStorageLocation));
            System.out.println("==========================================");
        } catch (Exception ex) {
            System.err.println("Erreur lors de la création du répertoire: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Impossible de créer le répertoire de stockage des fichiers.", ex);
        }
    }

    public String storeFile(MultipartFile file) {

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                throw new RuntimeException("Le fichier est vide: " + originalFileName);
            }

            if(originalFileName.contains("..")) {
                throw new RuntimeException("Le nom du fichier contient une séquence de chemin invalide " + originalFileName);
            }

            String fileExtension = "";
            if(originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Vérifier que le répertoire existe
            if (!Files.exists(this.fileStorageLocation)) {
                System.out.println("Création du répertoire: " + this.fileStorageLocation);
                Files.createDirectories(this.fileStorageLocation);
            }

            // Copier le fichier vers l'emplacement cible
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            System.out.println("Sauvegarde du fichier vers: " + targetLocation.toAbsolutePath());
            
            // Vérifier la taille du fichier
            long fileSize = file.getSize();
            System.out.println("Taille du fichier: " + fileSize + " bytes");
            
            // S'assurer que le répertoire parent existe
            Files.createDirectories(targetLocation.getParent());
            
            // Copier le fichier vers l'emplacement cible
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Forcer l'écriture sur le disque
            try {
                Files.getFileStore(targetLocation).getAttribute("basic:isReadOnly");
            } catch (Exception e) {
                // Ignorer les erreurs de vérification
            }
            
            // Vérifier que le fichier a bien été créé
            if (Files.exists(targetLocation)) {
                long savedFileSize = Files.size(targetLocation);
                System.out.println("✅ Fichier sauvegardé avec succès: " + newFileName);
                System.out.println("   Chemin complet: " + targetLocation.toAbsolutePath());
                System.out.println("   Taille: " + savedFileSize + " bytes");
                System.out.println("   Fichier existe: " + Files.exists(targetLocation));
                System.out.println("   Fichier est lisible: " + Files.isReadable(targetLocation));
                
                // Vérification supplémentaire après un court délai
                try {
                    Thread.sleep(100);
                    if (!Files.exists(targetLocation)) {
                        System.err.println("⚠️ ATTENTION: Le fichier a disparu après la sauvegarde!");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.err.println("❌ ERREUR: Le fichier n'a pas été créé: " + targetLocation);
                throw new RuntimeException("Le fichier n'a pas été créé: " + targetLocation);
            }

            return newFileName;
        } catch (IOException ex) {
            System.err.println("Erreur lors de la sauvegarde du fichier " + originalFileName + ": " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Impossible de stocker le fichier " + originalFileName + ". Veuillez réessayer!", ex);
        }
    }
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

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Impossible de supprimer le fichier " + fileName, ex);
        }
    }

    public void deleteFiles(List<String> fileNames) {
        if (fileNames != null && !fileNames.isEmpty()) {
            for (String fileName : fileNames) {
                deleteFile(fileName);
            }
        }
    }

    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }
}
