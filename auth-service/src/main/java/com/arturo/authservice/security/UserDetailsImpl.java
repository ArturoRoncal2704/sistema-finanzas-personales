package com.arturo.authservice.security;

import com.arturo.authservice.entity.User;
import com.arturo.authservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//La interfaz UserDetails define lo minimo que Spring debe saber sobre un usuario
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Boolean enabled;

    //Convierte una entidad User en un objeto UserDetailsImpl
    public static UserDetails build (User user){
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getEnabled()
        );
    }

    //Los roles se manejan como una lista de GrantedAuthority
    //Aquí convierte tu Role Enum en un formato que spring entienda "ROLE_AMDIN" o "ROLE_USER"
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.singletonList( //Crea una lista con una sola autoridad, porque cada usuario aquí tiene solo un rol
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
