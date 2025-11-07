package com.arturo.authservice.service.impl;

import com.arturo.authservice.dto.request.LoginRequest;
import com.arturo.authservice.dto.request.RegisterRequest;
import com.arturo.authservice.dto.response.AuthResponse;
import com.arturo.authservice.dto.response.UserDTO;
import com.arturo.authservice.entity.User;
import com.arturo.authservice.enums.Role;
import com.arturo.authservice.exception.BadRequestException;
import com.arturo.authservice.exception.ResourceNotFoundException;
import com.arturo.authservice.repository.UserRepository;
import com.arturo.authservice.security.JwtTokenProvider;
import com.arturo.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service //Marca la clase como servicio de negocio, gestionado por Spring
@RequiredArgsConstructor //Genera un constructor automatico para inyectar todas las dependencias final
@Slf4j // Crea automaticamente un logger(log.info(), log.error(),etc) para registrar eventos en consola o archivos
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository; //Accede a la base de datos para consultar o guardar usuarios
    private final PasswordEncoder passwordEncoder; //Cifra contraseñas(usa BCrypt)
    private final AuthenticationManager authenticationManager; //Autentica usuarios(verifica credenciales)
    private final JwtTokenProvider jwtTokenProvider; //Genera y valida tokens JWT

    @Override
    public AuthResponse register(RegisterRequest request) {
        //Registra en logs la acción para monitoreo
        log.info("Registrando un nuevo usuario: {}", request.getUsername());

        //Verificar si el username ya existe
        if(userRepository.existsByUsername(request.getUsername())){
            throw new BadRequestException("El nombre de usuario ya está en uso.");
        }
        //Verificar si el email ya existe
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("El email ya está en uso.");
        }

        //Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);
        user.setEnabled(true);

        //Guarda en la BD
        User savedUser = userRepository.save(user);

        log.info("Usuario registrado exitosamente: {}", savedUser.getId());

        //Autentica al usuario recién creado para emitir su JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        //Crea el token con JWTTokenProvider
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        //Devuelve AuthResponse con los datos del usuario + token
        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );
    }
    //Registro:
    //-> valida -> guarda usuario -> autentica -> genera JWT -> responde con token
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Usuario intentando iniciar sesión: {}", request.getUsername());

        //Spring valida el usuario y la contraseña(si fallan,lanza BadCredentialsException)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        //Guarda autenticación
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Generar token JWT
        String jwt = jwtTokenProvider.generateToken(authentication);

        //Buscar datos del usuario
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        log.info("Usuario ha iniciado sesión correctamente: {}", user.getUsername());
        //Respuesta
        return new AuthResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    //Login
    //->autentica -> genera JWT -> responde con token
    @Override
    public UserDTO getCurrentUser(String username) {
        log.info("Obtener información de usuario actual: {}", username);
        //Buscar el usuario en BD
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        //Convertir a DTO
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }
    //Get user
    //-> busca en BD -> devuelve DTO sin contraseña
}
