package com.arturo.authservice.exception;

public class ResourceNotFoundException extends RuntimeException{ //Crea una excepción personalizada no comprobada.

    public ResourceNotFoundException(String message) { //Permite lanzar la excepción con un mensaje.
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) { //Encadena errores y conserva la causa original.
        super(message, cause);
    }
}
