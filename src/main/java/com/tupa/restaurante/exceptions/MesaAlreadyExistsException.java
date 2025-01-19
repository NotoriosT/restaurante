package com.tupa.restaurante.exceptions;

public class MesaAlreadyExistsException extends RuntimeException {
    public MesaAlreadyExistsException(String mensagem) {
        super(mensagem);
    }
}
