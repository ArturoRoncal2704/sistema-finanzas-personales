package com.arturo.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter garantiza que el filtro se ejecute solo una vez por petición HTTP, evitando duplicaciones
    private final JwtTokenProvider jwtTokenProvider; //Se usa para validar y extraer datos del token
    private final UserDetailsServiceImpl userDetailsService; //Se usa para cargar al usuario desde la base de datos cuando es valido

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException { //Se ejecuta antes de que el request llegue al controlador
            try {
                //Llama a un metodo auxiliar que busca el token JWT en el header "Authorization"
                String jwt = getJwtFromRequest(request);
                //Verifica que el token no esté vacio
                //Confirma que el token no esté expirado ni manipulado
                //si todo va bien obtiene el nombre de usuario desde el token
                if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)){
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    //Busca en la base de datos los detalles del usuario
                    //Devuelve un objeto de UserDetailsImpl
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    //Crea un objeto de autenticación
                    //No incluye la contraseña "null" porque ya se valido con el token
                    //Incluye las autoridades (roles/permisos) del usuario
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    //Añade informacion extra sobre la petición(IP,navegador,etc.)
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    //Establece la autenticación en el contexto de seguridad actual
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ex){
                logger.error("No se pudo configurar la autenticación de usuario en el contexto de seguridad", ex);
            }
            //Deja que la petición siga su curso normal hacia el siguiente filtro o controlador
            filterChain.doFilter(request, response);
    }

    //Obtiene el valor del header "Authorization"
    //Si empieza con "Bearer ", devuelve el token sin el prefijo, los primeros 7 caracteres.
    //Si no existe o esta mal formado, devuelve null
    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
