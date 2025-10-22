package odk.SuguConnect.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI suguConnectOpenAPI() {
        // Nom du schéma de sécurité
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("API SuguConnect")
                        .description("API REST pour la plateforme SuguConnect - Mise en relation entre producteurs et consommateurs\n\n" +
                                "**Authentification JWT:**\n" +
                                "1. Utilisez `/auth/login/admin`, `/auth/login/producteur`, ou `/auth/login/consommateur` pour vous connecter\n" +
                                "2. Copiez le token JWT de la réponse\n" +
                                "3. Cliquez sur le bouton 🔓 **Authorize** en haut\n" +
                                "4. Collez le token (sans 'Bearer') et cliquez sur **Authorize**\n" +
                                "5. Vous pouvez maintenant tester les endpoints protégés")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe SuguConnect")
                                .email("contact@suguconnect.com")
                                .url("https://suguconnect.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                
                // Configuration de la sécurité JWT
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez le token JWT obtenu après connexion (sans le préfixe 'Bearer')")
                        )
                )
                
                // Ajouter la sécurité globalement à tous les endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/suguconnect")
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://api.suguconnect.com/suguconnect")
                                .description("Serveur de production")))
                .tags(List.of(
                        new Tag()
                                .name("Authentification")
                                .description("Connexion et gestion des tokens JWT"),
                        new Tag()
                                .name("Administrateur")
                                .description("Gestion des administrateurs et supervision de la plateforme (🔒 Token ADMIN requis)"),
                        new Tag()
                                .name("Producteur")
                                .description("Gestion des producteurs et de leurs produits (🔒 Token PRODUCTEUR ou ADMIN requis)"),
                        new Tag()
                                .name("Consommateur")
                                .description("Gestion des consommateurs, paniers et commandes (🔒 Token CONSOMMATEUR ou ADMIN requis)"),
                        new Tag()
                                .name("Catégorie")
                                .description("Gestion des catégories de produits"),
                        new Tag()
                                .name("Paiement")
                                .description("Gestion des paiements et transactions (🔒 Token CONSOMMATEUR ou ADMIN requis)"),
                        new Tag()
                                .name("Notification")
                                .description("Gestion des notifications utilisateurs avec actions et durée de vie (🔒 Token requis)")
                ));
    }
}
