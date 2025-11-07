package com.arturo.authservice.exception;

public class BadRequestException extends RuntimeException{ //Hereda las excepciones no comprobadas

    public BadRequestException(String message){ //Crea la exceipcioón con un mensaje personalizado
        super(message);
    }

    public BadRequestException(String message, Throwable cause){ //Añade una excepción original como causa del error
        super(message,cause);
    }
}
