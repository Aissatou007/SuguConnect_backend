package odk.SuguConnect.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequestDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String localisation;
    private long latitude;
    private long longitude;
    private String motDePasse;
}
