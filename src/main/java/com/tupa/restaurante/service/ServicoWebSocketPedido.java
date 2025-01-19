package com.tupa.restaurante.service;

import com.tupa.restaurante.dto.PedidoDTORetorno;
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
