package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.PedidoDTORetorno;
import com.tupa.restaurante.entities.Conta;
import com.tupa.restaurante.entities.Pedido;
import com.tupa.restaurante.exceptions.ClienteNaoEncontradoException;
import com.tupa.restaurante.service.ContaService;
import com.tupa.restaurante.service.PedidoService;
import com.tupa.restaurante.service.ServicoWebSocketPedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;
    private final ServicoWebSocketPedido servicoWebSocketPedido;
    private final PedidoService pedidoService;

    public ContaController(ContaService contaService, ServicoWebSocketPedido servicoWebSocketPedido, PedidoService pedidoService) {
        this.contaService = contaService;
        this.servicoWebSocketPedido = servicoWebSocketPedido;
        this.pedidoService = pedidoService;
    }

    @PostMapping("/abrir/{mesaId}")
    public ResponseEntity<Conta> abrirConta(@PathVariable String mesaId, @RequestParam String idCliente) {
        try {
            Conta conta = contaService.abrirConta(mesaId, idCliente);
            return ResponseEntity.ok(conta);
        } catch (ClienteNaoEncontradoException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{contaId}/pedidos")
    public ResponseEntity<PedidoDTORetorno> criarPedido(@PathVariable String contaId, @RequestBody Pedido novoPedido) {
        try {
            novoPedido.setDataPedido(LocalDateTime.now());
            Pedido pedidoCriado = contaService.criarPedido(contaId, novoPedido);

            List<PedidoDTORetorno> listaPedidosAtualizada = pedidoService.getAllPedidos();
            servicoWebSocketPedido.enviarPedidosAtualizadosParaCozinha(listaPedidosAtualizada);

            PedidoDTORetorno pedidoDTO = pedidoService.convertToPedidoDTO(pedidoCriado);
            return ResponseEntity.ok(pedidoDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{contaId}/fechar")
    public ResponseEntity<BigDecimal> fecharConta(@PathVariable String contaId) {
        try {
            BigDecimal total = contaService.fecharConta(contaId);
            return ResponseEntity.ok(total);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Conta>> obterTodasContas() {
        List<Conta> contas = contaService.getAllContas();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<List<Conta>> obterContasPorMesaId(@PathVariable String mesaId) {
        List<Conta> contas = contaService.getContasByMesaId(mesaId);
        return ResponseEntity.ok(contas);
    }
}
