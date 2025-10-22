package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import odk.SuguConnect.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@Tag(name = "Gestion des Fichiers", description = "API pour uploader, télécharger et supprimer des fichiers (images, documents)")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(
            summary = "Upload un fichier unique",
            description = "Permet d'uploader un seul fichier (image, document, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier uploadé avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier invalide ou vide")
    })
    public ResponseEntity<Map<String, String>> uploadFile(
            @Parameter(description = "Fichier à uploader", required = true)
            @RequestParam("file") MultipartFile file) {
        
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/suguconnect/files/download/")
                .path(fileName)
                .toUriString();

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileDownloadUri", fileDownloadUri);
        response.put("fileType", file.getContentType());
        response.put("size", String.valueOf(file.getSize()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-multiple")
    @Operation(
            summary = "Upload plusieurs fichiers",
            description = "Permet d'uploader plusieurs fichiers en une seule requête"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichiers uploadés avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichiers invalides ou vides")
    })
    public ResponseEntity<List<Map<String, String>>> uploadMultipleFiles(
            @Parameter(description = "Fichiers à uploader", required = true)
            @RequestParam("files") MultipartFile[] files) {
        
        List<Map<String, String>> responseList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = fileStorageService.storeFile(file);

                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/suguconnect/files/download/")
                        .path(fileName)
                        .toUriString();

                Map<String, String> response = new HashMap<>();
                response.put("fileName", fileName);
                response.put("fileDownloadUri", fileDownloadUri);
                response.put("fileType", file.getContentType());
                response.put("size", String.valueOf(file.getSize()));

                responseList.add(response);
            }
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/download/{fileName:.+}")
    @Operation(
            summary = "Télécharger un fichier",
            description = "Permet de télécharger un fichier par son nom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier téléchargé"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName,
            HttpServletRequest request) {
        
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName:.+}")
    @Operation(
            summary = "Supprimer un fichier",
            description = "Permet de supprimer un fichier du serveur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<Map<String, String>> deleteFile(
            @Parameter(description = "Nom du fichier à supprimer", required = true)
            @PathVariable String fileName) {
        
        fileStorageService.deleteFile(fileName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Fichier supprimé avec succès");
        response.put("fileName", fileName);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/product/{productId}/upload-photos")
    @Operation(
            summary = "Upload les photos d'un produit",
            description = "Permet d'uploader plusieurs photos pour un produit spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photos uploadées avec succès"),
            @ApiResponse(responseCode = "400", description = "Photos invalides")
    })
    public ResponseEntity<Map<String, Object>> uploadProductPhotos(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable int productId,
            @Parameter(description = "Photos du produit", required = true)
            @RequestParam("photos") MultipartFile[] photos) {
        
        List<String> photoUrls = new ArrayList<>();

        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String fileName = fileStorageService.storeFile(photo);
                
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/suguconnect/files/download/")
                        .path(fileName)
                        .toUriString();
                
                photoUrls.add(fileDownloadUri);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("photoUrls", photoUrls);
        response.put("totalPhotos", photoUrls.size());

        return ResponseEntity.ok(response);
    }
}
