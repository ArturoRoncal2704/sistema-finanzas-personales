package com.arturo.authservice.entity;

import com.arturo.authservice.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50) //Define restricciones
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false) //Campo obligatorio no puede ser nulo
    private String password;

    @Column(name = "first_name" , length = 50)
    private String firstName;

    @Column(name="last_name" , length = 50)
    private String lastName;

    @Enumerated(EnumType.STRING) //Guarda el enum Role como texto ("USER", "ADMIN") en la BD
    @Column(nullable = false)
    private Role role = Role.USER; //Rol por defecto USER

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name="created_at",nullable = false,updatable = false) //guarda la fecha de creación no se puede actualizar
    private LocalDateTime createdAt;

    @Column(name="updated_at") //Guarda la ultima fecha de modificación
    private LocalDateTime updatedAt;

    @PrePersist //Se ejecuta automaticamente antes de guardar el objeto por primera vez
    protected void onCreate() { //Inicializa created y updated con la fecha actual
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate //Se ejecuta antes de una actualización en la BD
    protected void onUpdate() { //Actualiza la fecha update a la hora actual
        updatedAt = LocalDateTime.now();
    }

}
