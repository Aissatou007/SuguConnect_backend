package odk.SuguConnect.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtre d'authentification JWT pour intercepter et valider les tokens
 * S'exécute une fois par requête pour extraire et valider le token JWT
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Ajouter un log pour voir toutes les requêtes
        logger.info("=== Requête entrante ===");
        logger.info("Méthode: " + request.getMethod());
        logger.info("URL: " + request.getRequestURL());
        logger.info("Query String: " + request.getQueryString());
        
        // Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String telephone;

        // Vérifier si le header existe et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("Pas de header Authorization ou pas de Bearer token");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraire le token JWT (après "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            // Extraire le téléphone (username) du token
            telephone = jwtService.extractUsername(jwt);
            logger.info("JWT Filter - Telephone extrait: " + telephone);

            // Si le téléphone existe et qu'il n'y a pas d'authentification en cours
            if (telephone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Vérifier si le token est valide
                if (jwtService.isTokenValid(jwt, telephone)) {
                    logger.info("JWT Filter - Token valide pour: " + telephone);
                    
                    // Extraire le rôle directement du token JWT
                    String role = jwtService.extractRole(jwt);
                    logger.info("JWT Filter - Rôle extrait du token: " + role);
                    
                    // Extraire l'ID utilisateur du token JWT
                    Integer userId = jwtService.extractUserId(jwt);
                    logger.info("JWT Filter - ID utilisateur extrait du token: " + userId);
                    
                    // Créer l'autorité avec le préfixe ROLE_
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    logger.info("JWT Filter - Autorité créée: " + authority.getAuthority());
                    
                    // Créer un UserDetails simplifié à partir du token
                    UserDetails userDetails = User.builder()
                            .username(telephone)
                            .password("") // Pas besoin du mot de passe pour JWT
                            .authorities(Collections.singletonList(authority))
                            .build();
                    
                    // Créer un token d'authentification
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Ajouter les détails de la requête
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Mettre à jour le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JWT Filter - Authentification définie dans SecurityContext avec autorités: " + authToken.getAuthorities());
                } else {
                    logger.warn("JWT Filter - Token invalide pour: " + telephone);
                }
            } else if (telephone == null) {
                logger.warn("JWT Filter - Téléphone null dans le token");
            } else {
                logger.info("JWT Filter - Authentification déjà présente dans SecurityContext");
            }
        } catch (Exception e) {
            // En cas d'erreur (token invalide, expiré, etc.), continuer sans authentification
            logger.error("Erreur lors de la validation du token JWT: " + e.getMessage(), e);
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}