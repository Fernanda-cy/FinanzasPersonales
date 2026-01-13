package com.example.FinanzasPersonales.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.FinanzasPersonales.model.UsuarioRol;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
	// Inyectamos los valores desde application.yml
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String correo, List<UsuarioRol> roles) {
        List<String> roleNames = roles.stream()
                .map(UsuarioRol::getRol)
                .toList();

        return Jwts.builder()
                .setSubject(correo)
                .claim("roles", roleNames)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // ... (El resto de métodos extractCorreo y extractRoles se mantienen igual)
    public String extractCorreo(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return (List<String>) claims.get("roles");
    }
    
    public boolean validateToken(String token, String correo) {
        final String extractedCorreo = extractCorreo(token);
        // Aquí usamos el método: esto quitará el color amarillo por "no usado"
        return (extractedCorreo.equals(correo) && !isTokenExpired(token));
    }

    // 2. Definir como private está bien, pero debe usarse arriba
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
    
}
