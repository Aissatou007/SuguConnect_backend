package odk.SuguConnect.DTO.Request;

public record ConsommateurRequestDTO(
        String nom ,
        String prenom ,
        String telephone ,
        String email ,
        String localisation,
        long latitude ,
        long longitude ,
        String motDePasse
) {
}
