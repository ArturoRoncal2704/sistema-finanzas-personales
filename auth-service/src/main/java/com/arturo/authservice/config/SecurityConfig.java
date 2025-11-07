package com.arturo.authservice.config;

import com.arturo.authservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //Indica que esta clase define beans
@EnableWebSecurity //Activa la seguridad web de Spring security
@EnableMethodSecurity //Permite usar anotaciones como @PreAuthorize o @Secured para proteger metodos especificos
@RequiredArgsConstructor //Crea automaticamente constructor para los campos final
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; //tu filtro personalizado que valida el token JWT en cada request
    private final UserDetailsService userDetailsService; //Serivico que carga los datos del usuario desde la base de datos

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Define que rutas estan protegidas que filtros usar y como se manejas las sesiones
        http
                .csrf(AbstractHttpConfigurer::disable) //Desactiva la proteccion CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register","/auth/login").permitAll() //Rutas públicas no requiere token
                        .requestMatchers("/actuator/**").permitAll() //Tambien público no requiere token
                        .anyRequest().authenticated() //Cualquier otra ruta requiere autenticación JWT
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //STATELESS no guarda sesión en el servidor, cada petición debe incluir su JWT
                )
                .authenticationProvider(authenticationProvider()) //Registra tu lógica de autenticación personalizada
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //Inserta tu filtro jwtAuthFilter antes del filtro estandar de Spring
                //Para que el JWT se valide antes de procesar la autenticación

        return http.build(); //Devuelve el objeto SecurityFilterChain configurado
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
        //Usa DaoAuthenticationProvider, que carga usuarios de la base de datos, verifica la contraseña cifrada
        //Basicamente le enseña a Spring cómo autenticar usuarios con tu sistema
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //Usa BCrypt, un algoritmo seguro para encriptar contraseñas.
        //Siempre que registre o compare contraseñas Spring usará este encoder
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
    }
    //Devuelve el AuthenticationManager global configurado por Spring
    //Es el que se usa en tus controladores de login para autenticar usuarios manualmente

}
