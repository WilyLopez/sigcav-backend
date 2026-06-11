package com.jmcavel.sigcav.exception;

public class DocumentoDuplicadoException extends RuntimeException {

    public DocumentoDuplicadoException(String mensaje) {
        super(mensaje);
    }

    public DocumentoDuplicadoException(String campo, String valor) {
        super("Ya existe un registro con " + campo + ": " + valor);
    }
}