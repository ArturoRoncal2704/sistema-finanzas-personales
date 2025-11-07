package com.arturo.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component //Permite inyectar JwtTokenProvider donde se necesite
public class JwtTokenProvider {

    //Estos valores se leen del archivo application.propeties o application.yml
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) { //Construye y firma el token para el usuario autenticado

        //Obtiene la implementación de UserDetails con username, id , email, role
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        //Emitido en
        Date now = new Date();
        //Expira en
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        //Crea una SecretKey apartir del jwtSecret
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        //Construcción del JWT
        return Jwts.builder()
                .subject(userPrincipal.getUsername()) //el username
                .claim("userId", userPrincipal.getId()) // claims personalizados
                .claim("email", userPrincipal.getEmail())
                .claim("role", userPrincipal.getRole().name())
                .issuedAt(now) // Añaden control temporal estándar
                .expiration(expiryDate)
                .signWith(key) // Firma el token, algoritmo se deduce por la clave
                .compact(); // devuelve el String header.payload.signature
    }

    public String getUsernameFromToken(String token) {
        //Clave
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key) //Configura verificación con tu clave
                .build()
                .parseSignedClaims(token) // verifica firma y parsea
                .getPayload(); //obtiene los claims

        return claims.getSubject(); //Devuelve el username guardado como sub
    }

    public Long getUserIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("userId", Long.class); //Extrae el claim personalizado userID como Long
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token); // Si firma/fechas/formato son correctos, no lanza excepción

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; //token inválido, expiro, mal formado o firma incorrecta
        }
    }
            //Devuelve true si el token pasa firma + fechas + formato.
            //Devuelve false si ocurre cualquier problema típico de JWT.

}
