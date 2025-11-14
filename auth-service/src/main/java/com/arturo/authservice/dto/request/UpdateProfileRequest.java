package com.arturo.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "El primer nombre es requerido")
    @Size(max = 50, message = "El primer nombre es demasiado largo")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 50, message = "El apellido es demasiado largo")
    private String lastName;
}
