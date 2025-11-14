package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import odk.SuguConnect.Service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/thumbnails")
@Tag(name = "Vignettes", description = "API pour récupérer les vignettes des images")
public class ThumbnailController {

    private final FileStorageService fileStorageService;

    public ThumbnailController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/product/{fileName:.+}")
    @Operation(
            summary = "Récupérer la vignette d'une image de produit",
            description = "Retourne la vignette d'une image de produit avec une taille optimisée pour l'affichage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vignette récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Vignette non trouvée")
    })
    public ResponseEntity<Resource> getProductThumbnail(
            @Parameter(description = "Nom du fichier image", required = true)
            @PathVariable String fileName,
            HttpServletRequest request) {
        
        try {
            // Construire le nom du fichier de vignette
            String thumbnailFileName = "thumb_" + fileName;
            
            // Charger la vignette
            Resource resource = fileStorageService.loadFileAsResource(thumbnailFileName);
            
            // Déterminer le type de contenu
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Si la vignette n'existe pas, retourner l'image originale
            try {
                Resource originalResource = fileStorageService.loadFileAsResource(fileName);
                String contentType = request.getServletContext().getMimeType(originalResource.getFile().getAbsolutePath());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + originalResource.getFilename() + "\"")
                        .body(originalResource);
            } catch (Exception ex) {
                return ResponseEntity.notFound().build();
            }
        }
    }
    
    @GetMapping("/small/{fileName:.+}")
    @Operation(
            summary = "Récupérer une petite vignette",
            description = "Retourne une vignette de petite taille (100x100) pour les listes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Petite vignette récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Vignette non trouvée")
    })
    public ResponseEntity<Resource> getSmallThumbnail(
            @Parameter(description = "Nom du fichier image", required = true)
            @PathVariable String fileName,
            HttpServletRequest request) {
        
        try {
            // Construire le nom du fichier de vignette
            String thumbnailFileName = "thumb_small_" + fileName;
            
            // Charger la vignette
            Resource resource = fileStorageService.loadFileAsResource(thumbnailFileName);
            
            // Déterminer le type de contenu
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            // Si la vignette n'existe pas, essayer avec la vignette normale
            try {
                return getProductThumbnail(fileName, request);
            } catch (Exception ex) {
                return ResponseEntity.notFound().build();
            }
        }
    }
}