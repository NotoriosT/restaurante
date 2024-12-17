package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.entidades.Conta;
import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.services.ContaService;
import com.tupa.restaurante.services.PedidoService;
import com.tupa.restaurante.services.ServicoWebSocketPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ServicoWebSocketPedido servicoWebSocketPedido;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Abre uma nova conta para uma mesa específica.
     *
     * @param mesaId O ID da mesa.
     * @return A conta criada.
     */
    @PostMapping("/abrir/{mesaId}")
    public Conta abrirConta(@PathVariable String mesaId) {
        return contaService.abrirConta(mesaId);
    }

    /**
     * Cria um novo pedido para uma conta específica, retorna o DTO do pedido e notifica a cozinha via WebSocket.
     *
     * @param contaId    O ID da conta.
     * @param novoPedido O novo pedido a ser criado.
     * @return O DTO do pedido criado.
     */
    @PostMapping("/{contaId}/pedidos")
    public PedidoDTORetorno criarPedido(@PathVariable String contaId, @RequestBody Pedido novoPedido) {
        Pedido pedidoCriado = contaService.criarPedido(contaId, novoPedido);
        // Recupera a lista atualizada de pedidos como DTOs
        List<PedidoDTORetorno> listaPedidosAtualizada = pedidoService.getAllPedidos();
        // Envia a lista atualizada para a cozinha via WebSocket
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);
        // Retorna o DTO do pedido criado
        return pedidoService.convertToPedidoDTO(pedidoCriado);
    }

    /**
     * Fecha uma conta específica e retorna o valor total.
     *
     * @param contaId O ID da conta.
     * @return O valor total da conta.
     */
    @PostMapping("/{contaId}/fechar")
    public double fecharConta(@PathVariable String contaId) {
        return contaService.fecharConta(contaId);
    }

    /**
     * Recupera todas as contas abertas.
     *
     * @return Uma lista de todas as contas.
     */
    @GetMapping
    public List<Conta> obterTodasContas() {
        return contaService.getAllContas();
    }

    /**
     * Recupera todas as contas associadas a uma mesa específica.
     *
     * @param mesaId O ID da mesa.
     * @return Uma lista de contas associadas à mesa.
     */
    @GetMapping("/mesa/{mesaId}")
    public List<Conta> obterContasPorMesaId(@PathVariable String mesaId) {
        return contaService.getContasByMesaId(mesaId);
    }
}
