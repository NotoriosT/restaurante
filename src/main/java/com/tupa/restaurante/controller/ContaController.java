package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.entidades.Conta;
import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.exeptions.ClienteNaoEncontradoException;
import com.tupa.restaurante.services.ContaService;
import com.tupa.restaurante.services.PedidoService;
import com.tupa.restaurante.services.ServicoWebSocketPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // ------------------------------------------------------------
    // Abertura de conta:
    @PostMapping("/abrir/{mesaId}")
    public ResponseEntity<Conta> abrirConta(@PathVariable String mesaId, @RequestParam String idCliente) {
        try {
            Conta conta = contaService.abrirConta(mesaId, idCliente);
            return ResponseEntity.ok(conta);
        } catch (ClienteNaoEncontradoException e) {
            // Trate apropriadamente
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Tratar outras exceções
            return ResponseEntity.internalServerError().build();
        }
    }

    // ------------------------------------------------------------
    // Criar pedido na conta:
    @PostMapping("/{contaId}/pedidos")
    public PedidoDTORetorno criarPedido(@PathVariable String contaId, @RequestBody Pedido novoPedido) {
        novoPedido.setDataPedido(LocalDateTime.now());
        Pedido pedidoCriado = contaService.criarPedido(contaId, novoPedido);

        // Recupera a lista atualizada de pedidos como DTOs
        List<PedidoDTORetorno> listaPedidosAtualizada = pedidoService.getAllPedidos();

        // Envia a lista atualizada para a cozinha via WebSocket
        servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

        // Retorna o DTO do pedido criado
        return pedidoService.convertToPedidoDTO(pedidoCriado);
    }

    // ------------------------------------------------------------
    // Fechar conta:
    @PostMapping("/{contaId}/fechar")
    public BigDecimal fecharConta(@PathVariable String contaId) {
        return contaService.fecharConta(contaId);
    }

    // ------------------------------------------------------------
    // Listar todas as contas abertas:
    @GetMapping
    public List<Conta> obterTodasContas() {
        return contaService.getAllContas();
    }

    // ------------------------------------------------------------
    // Listar contas de uma mesa específica:
    @GetMapping("/mesa/{mesaId}")
    public List<Conta> obterContasPorMesaId(@PathVariable String mesaId) {
        return contaService.getContasByMesaId(mesaId);
    }

} // <-- observe que a classe termina aqui, sem nada 'solto' depois
