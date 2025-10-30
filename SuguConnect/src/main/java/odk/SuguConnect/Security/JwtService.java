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
    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;
    

    public String generateToken(Map<String, Object> extraClaims, String telephone) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(telephone)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }
    

    public String generateToken(String telephone, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());
        return generateToken(claims, telephone);
    }
    

    public String generateToken(int userId, String telephone, String nom, String prenom, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("nom", nom);
        claims.put("prenom", prenom);
        claims.put("role", role.name());
        return generateToken(claims, telephone);
    }
    

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    public boolean isTokenValid(String token, String telephone) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(telephone) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    

    public long getTimeUntilExpiration(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}
