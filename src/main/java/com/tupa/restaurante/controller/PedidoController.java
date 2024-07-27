package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.entidades.Status;
import com.tupa.restaurante.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findByStatusIn(List.of(Status.PEDENTE, Status.PREPARANDO));
    }

    @PostMapping
    public Pedido createPedido(@RequestBody Pedido pedido) {
        Pedido savedPedido = pedidoRepository.save(pedido);
        messagingTemplate.convertAndSend("/topic/pedidos", savedPedido);
        return savedPedido;
    }

    @PutMapping("/{id}/status")
    public Pedido updateStatus(@PathVariable String id, @RequestParam Status status) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(status);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        messagingTemplate.convertAndSend("/topic/pedidos", updatedPedido);
        return updatedPedido;
    }
}
