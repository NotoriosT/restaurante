package com.tupa.restaurante.exceptions;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(String mensagem) {
        super(mensagem);
    }
}
