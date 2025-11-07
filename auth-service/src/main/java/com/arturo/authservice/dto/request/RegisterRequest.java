package com.arturo.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es requerido.")
    @Size(min = 3, max = 20, message = "El nombre del usuario debe tener entre 3 y 20 caracteres.")
    private String username;

    @NotBlank(message = "El correo electrónico es requerido.")
    @Email(message = "El correo electrónico debe ser válido.")
    private String email;

    @NotBlank(message = "La contraseña es requerida.")
    @Size(min = 6 ,message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String firstName;

    private String lastName;

}
