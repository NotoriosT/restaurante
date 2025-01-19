package com.tupa.restaurante.exceptions;

public class ProdutoPedidoNotFoundException extends RuntimeException {
    public ProdutoPedidoNotFoundException(String mensagem) {
        super(mensagem);
    }
}
