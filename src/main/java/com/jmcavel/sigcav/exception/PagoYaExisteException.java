package com.jmcavel.sigcav.exception;

public class PagoYaExisteException extends RuntimeException {
    public PagoYaExisteException(String message) {
        super(message);
    }
}