package com.arturo.authservice.security;

import com.arturo.authservice.entity.User;
import com.arturo.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service //Indica que esta clase es un servicio de negocio
@RequiredArgsConstructor // Genera un constructor automatico para los campos final
public class UserDetailsServiceImpl implements UserDetailsService { //Define el contrato que usa el sistema de seguridad para buscar usuarios en tu base de datos

    private final UserRepository userRepository; //Acceso a la tabla users de la BD

    @Override
    @Transactional //
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username) //Usa el UserRepository para devolver un User, si el usuario no existe lanza una excepción
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con el nombre de usuario: " +  username));

        return UserDetailsImpl.build(user);
        //Convierte tu entidad User en un objeto de tipo UserDetailsImpl, que ela implementación personalizada de la interfaz UserDetails
        //Spring puede comprar la contraseña del token de login con la guardada en BD y autorizar acceso
    }
}
