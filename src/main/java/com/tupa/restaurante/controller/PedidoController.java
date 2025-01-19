package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.service.PedidoService;
import com.tupa.restaurante.service.ServicoWebSocketPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAnyRole('COZINHA', 'GERENTE')")
    public PedidoDTORetorno avancarEstadoPedido(@PathVariable String idPedido) {
        PedidoDTORetorno pedidoAtualizado = pedidoService.avancarEstadoPedido(idPedido);

        // Enviar a lista atualizada para a cozinha via WebSocket
        List<PedidoDTORetorno> listaPedidosAtualizada = pedidoService.getAllPedidos();
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

        return pedidoAtualizado;
    }

    /**
     * Endpoint para cancelar uma quantidade específica de um produto dentro de um pedido.
     *
     * @param idPedido        ID do pedido.
     * @param idProdutoPedido ID do ProdutoPedido dentro do pedido.
     * @param quantidade      Quantidade a ser cancelada.
     * @param motivo          Motivo do cancelamento (opcional).
     * @return Pedido atualizado como DTO.
     */
    @PutMapping("/{idPedido}/cancelar-produto")
    @PreAuthorize("hasAnyRole('COZINHA', 'GERENTE')") // Ajuste os roles conforme sua aplicação
    public PedidoDTORetorno cancelarProdutoNoPedido(
            @PathVariable String idPedido,
            @RequestParam String idProdutoPedido,
            @RequestParam int quantidade,
            @RequestParam(required = false) String motivo) {

        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioAtual = authentication.getName();

        return pedidoService.cancelarProdutoNoPedido(idPedido, idProdutoPedido, quantidade, usuarioAtual, motivo);
    }
    @GetMapping("/conta/{idConta}")

    public ResponseEntity<List<PedidoDTORetorno>> getPedidosByConta(@PathVariable String idConta) {
        List<PedidoDTORetorno> pedidos = pedidoService.getPedidosByContaId(idConta);
        return ResponseEntity.ok(pedidos);
    }
}
