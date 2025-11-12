package com.arturo.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component // Indica que esta clase es un componente manejado por Spring (puede ser inyectado con @Autowired)
public class JwtUtil {

    // Clave secreta usada para firmar y verificar los tokens JWT.
    // Se obtiene desde el archivo application.yml o application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
      Extrae todos los "claims" (información codificada dentro del token JWT)
      Un JWT se compone de tres partes: header, payload (claims) y firma.
     */
    public Claims extractAllClaims(String token) {
        // Se crea una clave HMAC usando la cadena secreta configurada
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        //  Se crea un parser (analizador) que usa la clave para verificar la firma del token
        //  el token es válido, devuelve el "payload" (claims) con toda la información
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token) // Valida y decodifica el token firmado
                .getPayload();            // Retorna el cuerpo del token (claims)
    }

    /*
      Obtiene el nombre de usuario (subject) del token JWT.
      El "subject" es un campo estándar en los claims de un JWT.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
      Obtiene el ID del usuario desde los claims personalizados del token.
      Este dato se guarda normalmente en el payload cuando se genera el token.
     */
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    //Obtiene el correo electrónico del usuario desde el token JWT.

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // Obtiene el rol del usuario (por ejemplo: ADMIN, USER, etc.).

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /*Verifica si el token es válido.
      Básicamente intenta parsear los claims; si hay algún error (firma incorrecta o token expirado),
      se lanza una excepción y devuelve false.
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token); // Si no lanza excepción → el token es válido
            return true;
        } catch (Exception e) {
            return false; // Si hay error → token inválido o expirado
        }
    }
}