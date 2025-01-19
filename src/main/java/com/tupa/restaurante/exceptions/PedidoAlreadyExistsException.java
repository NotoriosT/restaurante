package com.tupa.restaurante.exceptions;

public class PedidoAlreadyExistsException extends RuntimeException {
    public PedidoAlreadyExistsException(String mensagem) {
        super(mensagem);
    }
}
