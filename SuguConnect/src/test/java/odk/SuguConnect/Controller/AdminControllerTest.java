package odk.SuguConnect.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import odk.SuguConnect.DTO.Request.AdminRequestDTO;
import odk.SuguConnect.DTO.Request.ProducteurRequestDTO;
import odk.SuguConnect.DTO.Responses.AdminResponseDTO;
import odk.SuguConnect.DTO.Responses.LivreurResponseDTO;
import odk.SuguConnect.Entity.Livreur;
import odk.SuguConnect.Enums.Role;
import odk.SuguConnect.Repository.AdminRepository;
import odk.SuguConnect.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AdminRepository adminRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAjouterProducteurAsAdmin() throws Exception {
        // Given
        ProducteurRequestDTO producteurDTO = new ProducteurRequestDTO(
                "Producteur", "Test", "test@example.com", "123456789", "password", "Localisation"
        );

        // When & Then
        mockMvc.perform(post("/admin/producteurs/ajouter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(producteurDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CONSOMMATEUR")
    public void testAjouterProducteurAsNonAdmin() throws Exception {
        // Given
        ProducteurRequestDTO producteurDTO = new ProducteurRequestDTO(
                "Producteur", "Test", "test@example.com", "123456789", "password", "Localisation"
        );

        // When & Then
        mockMvc.perform(post("/admin/producteurs/ajouter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(producteurDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testVoirLivreursPourCommande() throws Exception {
        // Given
        LivreurResponseDTO livreur1 = new LivreurResponseDTO(1, "Livreur 1", "Test", "MAT001", true);
        LivreurResponseDTO livreur2 = new LivreurResponseDTO(2, "Livreur 2", "Test", "MAT002", true);

        List<LivreurResponseDTO> livreurs = Arrays.asList(livreur1, livreur2);

        when(adminService.recupererLivreursDisponibles()).thenReturn(livreurs);

        // When & Then
        mockMvc.perform(get("/admin/commandes/livreurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}