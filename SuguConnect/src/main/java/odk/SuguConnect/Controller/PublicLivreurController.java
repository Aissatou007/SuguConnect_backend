package odk.SuguConnect.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Service.LivreurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/public/livreurs")
@RequiredArgsConstructor
@Tag(name = "Livreur (Public)", description = "API publique pour la récupération des livreurs")
public class PublicLivreurController {
    private final LivreurService livreurService;

    @GetMapping(path = "/all")
    @Operation(
            summary = "Récupérer tous les livreurs (public)",
            description = "Retourne la liste de tous les livreurs sans authentification requise"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livreurs récupérée")
    public ResponseEntity<List<LivreurResponseDTO>> recupererTousLesLivreursPublic() {
        List<LivreurResponseDTO> livreurs = livreurService.recupererTousLesLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @GetMapping(path = "/disponibles")
    @Operation(
            summary = "Récupérer les livreurs disponibles (public)",
            description = "Retourne la liste des livreurs actuellement disponibles sans authentification requise"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livreurs disponibles récupérée")
    public ResponseEntity<List<LivreurResponseDTO>> recupererLivreursDisponiblesPublic() {
        List<LivreurResponseDTO> livreurs = livreurService.recupererLivreursDisponibles();
        return ResponseEntity.ok(livreurs);
    }
}