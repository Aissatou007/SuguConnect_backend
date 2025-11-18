package odk.SuguConnect.DTO.Responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO pour les détails complets d'une catégorie")
public record CategorieResponseDTO(
        @Schema(description = "ID de la catégorie", example = "1")
        int id,
        
        @Schema(description = "Libellé de la catégorie", example = "Fruits et légumes")
        String libelle,
        
        @Schema(description = "Date d'ajout de la catégorie", example = "2025-10-23")
        LocalDate dateAjout,
        
        @Schema(description = "URL de la photo de la catégorie")
        String photoUrl
) {
}