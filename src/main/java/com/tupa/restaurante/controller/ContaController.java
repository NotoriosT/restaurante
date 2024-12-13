package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Conta;
import com.tupa.restaurante.entidades.Pedido;
import com.tupa.restaurante.services.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/abrir/{mesaId}")
    public Conta abrirConta(@PathVariable String mesaId) {
        return contaService.abrirConta(mesaId);
    }

    @PostMapping("/{contaId}/pedidos")
    public Pedido criarPedido(@PathVariable String contaId, @RequestBody Pedido novoPedido) {
        return contaService.criarPedido(contaId, novoPedido);
    }

    @PostMapping("/{contaId}/fechar")
    public double fecharConta(@PathVariable String contaId) {
        return contaService.fecharConta(contaId);
    }
    @GetMapping
    public List<Conta> getAllContas() {
        return contaService.getAllContas();
    }
    @GetMapping("/mesa/{mesaId}")
    public List<Conta> getContasByMesaId(@PathVariable String mesaId) {
        return contaService.getContasByMesaId(mesaId);
    }

}
