package odk.SuguConnect.Controller;

import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    private AdminService adminService;

    @PostMapping(path = "/inscription")
    public ResponseEntity<String> inscription(
            @RequestBody AdminRequestDTO adminRequestDTO){
        String message = adminService.inscriptionAdmin(adminRequestDTO, adminRequestDTO.telephone());
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/admins")
    public ResponseEntity<List<AdminResponseDTO>> recupererTousLesAdmins(){
        List<AdminResponseDTO> admins = adminService.recupererLesAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AdminResponseDTO> recupererUnAdmin(@PathVariable int id){
        AdminResponseDTO admin =  adminService.recupererUnAdmin(id);
        return ResponseEntity.ok(admin);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<String> modifier(@PathVariable int id ,
                                           @RequestBody AdminRequestDTO adminRequestDTO){
        String message = adminService.modifierInformationAdmin(adminRequestDTO , id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> supprimer(@PathVariable int id){
        String message = adminService.supprimerAdmin(id);
        return ResponseEntity.ok(message);
    }
}
