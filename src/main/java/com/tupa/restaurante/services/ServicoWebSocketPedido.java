package com.tupa.restaurante.services;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.entidades.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoWebSocketPedido {

    @Autowired
    private SimpMessagingTemplate mensageriaTemplate;





    public void enviarPedidosAtualizadosParaCozinha(List<PedidoDTORetorno> listaPedidos) {
        mensageriaTemplate.convertAndSend("/topico/cozinha/pedidosAtualizados", listaPedidos);
    }
}
