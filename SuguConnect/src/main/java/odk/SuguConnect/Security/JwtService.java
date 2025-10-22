package odk.SuguConnect.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import odk.SuguConnect.Enums.Role;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000; // 24 heures
    
    /**
     * Générer un token JWT avec des claims personnalisés
     */
    public String generateToken(Map<String, Object> extraClaims, String telephone) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(telephone)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    /**
     * Générer un token JWT simple avec téléphone et rôle
     */
    public String generateToken(String telephone, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        return generateToken(claims, telephone);
    }
    
    /**
     * Générer un token JWT complet avec toutes les informations utilisateur
     */
    public String generateToken(int userId, String telephone, String nom, String prenom, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nom", nom);
        claims.put("prenom", prenom);
        claims.put("role", role.name());
        return generateToken(claims, telephone);
    }
    
    /**
     * Extraire le téléphone (username) du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extraire un claim spécifique du token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extraire tous les claims du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Extraire le rôle du token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * Extraire l'ID utilisateur du token
     */
    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }
    
    /**
     * Vérifier si le token est valide
     */
    public boolean isTokenValid(String token, String telephone) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(telephone) && !isTokenExpired(token));
    }
    
    /**
     * Vérifier si le token a expiré
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extraire la date d'expiration du token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Obtenir le temps restant avant expiration (en millisecondes)
     */
    public long getTimeUntilExpiration(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}
