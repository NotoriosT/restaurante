package com.tupa.restaurante.exceptions;

public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(String mensagem) {
        super(mensagem);
    }
}

