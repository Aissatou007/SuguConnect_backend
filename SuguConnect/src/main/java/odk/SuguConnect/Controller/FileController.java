package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller responsable de la gestion des fichiers uploadés
 * Endpoints pour servir les fichiers de messages, images, etc.
 */
@RestController
@RequestMapping(path = "/files")
@Tag(name = "File", description = "API de gestion des fichiers uploadés")
public class FileController {

    @GetMapping("/messages/{type}/{filename:.+}")
    @Operation(
            summary = "Télécharger un fichier de message",
            description = "Télécharger un fichier (image, vocal, document) associé à un message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    public ResponseEntity<Resource> downloadMessageFile(
            @Parameter(description = "Type de fichier (images, voice, files)", required = true)
            @PathVariable String type,
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get("uploads/messages").resolve(type).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                String contentType = getContentType(filename);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String getContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowerFilename.endsWith(".wav")) {
            return "audio/wav";
        } else if (lowerFilename.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        } else {
            return "application/octet-stream";
        }
    }
}