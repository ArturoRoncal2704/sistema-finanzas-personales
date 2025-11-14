package com.arturo.authservice.service;

import com.arturo.authservice.dto.request.ChangePasswordRequest;
import com.arturo.authservice.dto.request.LoginRequest;
import com.arturo.authservice.dto.request.RegisterRequest;
import com.arturo.authservice.dto.request.UpdateProfileRequest;
import com.arturo.authservice.dto.response.AuthResponse;
import com.arturo.authservice.dto.response.UserDTO;


//Es la capa intermedia entre el controlador y la lógica de negocio(Repositorios, seguridad, etc)
public interface AuthService {

    //Crea un nuevo usuario, guarda en BD y devuelve un token JWT
    AuthResponse register(RegisterRequest request);
    //Valida credenciales, genera y devuelve un token JWT
    AuthResponse login(LoginRequest request);
    //Obtiene los datos del usuario autenticado desde la BD
    UserDTO getCurrentUser(String username);

    UserDTO updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}

