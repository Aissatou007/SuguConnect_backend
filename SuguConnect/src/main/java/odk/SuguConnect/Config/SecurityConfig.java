package odk.SuguConnect.Config;

import lombok.RequiredArgsConstructor;
import odk.SuguConnect.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // ENDPOINTS PUBLICS (Pas d'authentification)
                        // ============================================
                        
                        // Swagger/OpenAPI Documentation
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        
                        // Authentification (Login/Validation)
                        .requestMatchers("/auth/**").permitAll()
                        
                        // Inscriptions (Consommateur et Producteur)
                        .requestMatchers(
                                "/consommateur/inscription"
                        ).permitAll()
                        
                        // Producteur inscription now restricted to ADMIN only
                        .requestMatchers(
                                "/producteur/inscription"
                        ).hasRole("ADMIN")
                        
                        // Consultation publique - Liste des producteurs
                        .requestMatchers(
                                "/producteur/producteurs",         // Liste des producteurs
                                "/files/download/**"               // Téléchargement de fichiers (images, etc.)
                        ).permitAll()
                        
                        // Consultation publique - Produits disponibles (GET uniquement)
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/consommateur/produits"          // Voir les produits disponibles
                        ).permitAll()
                        
                        // Catégories publiques (GET uniquement)
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/categorie",
                                "/categorie/{id}",
                                "/categorie/{id}/produits"
                        ).permitAll()
                        
                        // Producteur public (GET uniquement)
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/producteur/{id}"
                        ).permitAll()
                        
                        // ============================================
                        // ENDPOINTS FICHIERS (Upload/Delete nécessite authentification)
                        // ============================================
                        .requestMatchers("/files/upload", "/files/upload-multiple").authenticated()
                        .requestMatchers("/files/delete/**").authenticated()
                        .requestMatchers("/files/product/*/upload-photos").authenticated()
                        
                        // ============================================
                        // ENDPOINTS ADMIN (Role ADMIN uniquement)
                        // ============================================
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        
                        // Catégories - Création, Modification, Suppression (ADMIN uniquement)
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/categorie"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/categorie/{id}"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/categorie/{id}"
                        ).hasRole("ADMIN")
                        
                        // ============================================
                        // ENDPOINTS PRODUCTEUR (PRODUCTEUR ou ADMIN)
                        // ============================================
                        .requestMatchers("/producteur/**").hasAnyRole("PRODUCTEUR", "ADMIN")
                        
                        // ============================================
                        // ENDPOINTS CONSOMMATEUR (CONSOMMATEUR ou ADMIN)
                        // ============================================
                        .requestMatchers("/consommateur/**").hasAnyRole("CONSOMMATEUR", "ADMIN")
                        
                        // ============================================
                        // ENDPOINTS PAIEMENT (CONSOMMATEUR ou ADMIN)
                        // ============================================
                        .requestMatchers("/api/paiements/**").hasAnyRole("CONSOMMATEUR", "ADMIN")
                        
                        // ============================================
                        // ENDPOINTS NOTIFICATIONS (Utilisateurs authentifiés)
                        // ============================================
                        .requestMatchers("/notifications/**").authenticated()
                        
                        // Toute autre requête nécessite une authentification
                        .anyRequest().authenticated()
                )
                // Gestion de session stateless (JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Ajouter le fournisseur d'authentification
                .authenticationProvider(authenticationProvider())
                // Ajouter le filtre JWT avant le filtre d'authentification standard
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fournisseur d'authentification personnalisé
     * Utilise le UserDetailsService et le PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestionnaire d'authentification
     * Nécessaire pour l'authentification manuelle dans les services
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
