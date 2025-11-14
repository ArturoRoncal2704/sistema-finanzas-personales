package com.arturo.authservice.controller;

import com.arturo.authservice.dto.request.LoginRequest;
import com.arturo.authservice.dto.request.RegisterRequest;
import com.arturo.authservice.dto.request.UpdateProfileRequest;
import com.arturo.authservice.dto.response.AuthResponse;
import com.arturo.authservice.dto.response.MessageResponse;
import com.arturo.authservice.dto.response.UserDTO;
import com.arturo.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController //Marca la clase como un controlador REST, combinado @Controlador + @ResponseBody. todos los métodos devuelven JSON
@RequestMapping("/auth") //Define el prefijo de las rutas. Todos los endpoints comienzan con /auth/...
@RequiredArgsConstructor
@Slf4j //Crea un logger para registrar información (mensajes, errores, etc)
public class AuthController {

    private final AuthService authService; //El servicio que contiene la logica

    @PostMapping("/register") //Expone un endpoint POST
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        //@RequestBody:Indica que los datos vienen en formato JSON en el cuerpo de la solicitud
        //@Valid: Activa las validaciones del DTO(@NotBlank, @Email,etc)
        log.info("Se recibió una solicitud de registro para el nombre de usuario.: {}", request.getUsername());
        //Registra solicitud en los logs
        AuthResponse response = authService.register(request);
        //Llama al servicio para registrar el usuario, cifrar contraseña y generar token JWT
        return new ResponseEntity<>(response, HttpStatus.CREATED); //Devuelve una respuesta con estado 201 Created
    }

    @PostMapping("/login") //Endpoint POST
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // DTO con username y password
        log.info("Se recibió una solicitud de inicio de sesión para el nombre de usuario: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        //Verifica credenciales y genera un token JWT
        return ResponseEntity.ok(response);
        //Deuelve estado 200 OK con el token y los datos del usuario
    }

    @GetMapping("/me") //Endpoint GET
    public ResponseEntity<UserDTO> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //SecurityContextHolder.getContext().getAuthentication(): Obtiene el usuario autenticado (cargado por el filtro JWT).
        String username = authentication.getName();
        // Devuelve el username del token JWT.
        log.info("Obtener la solicitud actual del usuario para : {}" , username);
        UserDTO userDTO = authService.getCurrentUser(username);
        //Busca la información del usuario en la BD.
        return ResponseEntity.ok(userDTO);
        //Devuelve la información con estado 200 OK.
    }

    //Actualizar perfil
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Solicitud de actualización de perfil de usuario: {}", userId);
        UserDTO response = authService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }
}
