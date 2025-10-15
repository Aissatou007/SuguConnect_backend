package odk.SuguConnect.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import odk.SuguConnect.Enums.TypeMessage;

import java.time.LocalDate;

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    private String titre ;
    private String message ;
    private TypeMessage typeMessage ;
    private LocalDate dateEnvoi ;

}
