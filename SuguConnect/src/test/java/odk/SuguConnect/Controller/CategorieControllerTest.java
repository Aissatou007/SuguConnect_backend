package odk.SuguConnect.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import odk.SuguConnect.DTO.Responses.CategorieResponseDTO;
import odk.SuguConnect.Entity.Categorie;
import odk.SuguConnect.Service.CategorieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategorieController.class)
public class CategorieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategorieService categorieService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreerCategorieWithPhoto() throws Exception {
        // Arrange
        String libelle = "Fruits";
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        Categorie categorie = new Categorie();
        categorie.setId(1);
        categorie.setLibelle(libelle);
        categorie.setDateAjout(LocalDate.now());
        categorie.setPhotoUrl("/suguconnect/files/download/test-image.jpg");

        when(categorieService.creerCategorie(anyString(), any())).thenReturn(categorie);

        // Act & Assert
        mockMvc.perform(multipart("/categorie")
                .part("libelle", libelle.getBytes())
                .file(photo)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.libelle").value(libelle))
                .andExpect(jsonPath("$.photoUrl").value("/suguconnect/files/download/test-image.jpg"));
    }

    @Test
    public void testCreerCategorieWithoutPhoto() throws Exception {
        // Arrange
        String libelle = "Légumes";

        Categorie categorie = new Categorie();
        categorie.setId(2);
        categorie.setLibelle(libelle);
        categorie.setDateAjout(LocalDate.now());
        categorie.setPhotoUrl(null);

        when(categorieService.creerCategorie(anyString(), any())).thenReturn(categorie);

        // Act & Assert
        mockMvc.perform(multipart("/categorie")
                .part("libelle", libelle.getBytes())
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.libelle").value(libelle))
                .andExpect(jsonPath("$.photoUrl").doesNotExist());
    }
}