package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.services.PedidoService;
import com.tupa.restaurante.services.ServicoWebSocketPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ServicoWebSocketPedido servicoWebSocketPedido;

    /**
     * Endpoint para obter todos os pedidos.
     *
     * @return Lista de todos os pedidos.
     */
    @GetMapping
    public List<PedidoDTORetorno> getAllPedidos() {
        return pedidoService.getAllPedidos();
    }

    /**
     * Endpoint para avançar o estado de um pedido.
     *
     * @param idPedido ID do pedido.
     * @return Pedido atualizado.
     */
    @PutMapping("/{idPedido}/avancar")
    public PedidoDTORetorno avancarEstadoPedido(@PathVariable String idPedido) {
        PedidoDTORetorno pedidoAtualizado = pedidoService.avancarEstadoPedido(idPedido);

        // Enviar a lista atualizada para a cozinha via WebSocket
        List<PedidoDTORetorno> listaPedidosAtualizada = pedidoService.getAllPedidos();
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

        return pedidoAtualizado;
    }
}
