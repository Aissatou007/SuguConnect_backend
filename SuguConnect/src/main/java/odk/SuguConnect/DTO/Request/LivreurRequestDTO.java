package odk.SuguConnect.DTO.Request;

import lombok.Builder;

@Builder
public record LivreurRequestDTO(
        String nom,
        String prenom,
        String telephone,
        String email,
        String localisation,
        String motDePasse,
        String matricule,
        String vehicule
) {
}