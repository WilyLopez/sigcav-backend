package com.jmcavel.sigcav.exception;

public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public EstadoInvalidoException(String estadoActual, String estadoDestino) {
        super("Transición de estado no permitida: " + estadoActual + " → " + estadoDestino);
    }
}