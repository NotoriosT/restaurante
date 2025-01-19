package com.tupa.restaurante.controller;

import com.tupa.restaurante.entities.Fechamento;
import com.tupa.restaurante.entities.Mesa;
import com.tupa.restaurante.service.FechamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fechamentos")
public class FechamentoController {

    @Autowired
    private FechamentoService fechamentoService;

    @PostMapping("/abrir")
    public Fechamento abrirFechamento() {
        return fechamentoService.abrirFechamento();
    }

    @PostMapping("/fechar")
    public Fechamento fecharFechamento() {
        return fechamentoService.fecharFechamento();
    }

    @GetMapping("/aberto")
    public Fechamento obterFechamentoAberto() {
        return fechamentoService.obterFechamentoAberto()
                .orElseThrow(() -> new IllegalStateException("Nenhum fechamento aberto no momento."));
    }

    @GetMapping
    public List<Fechamento> obterTodosFechamentos() {
        return fechamentoService.obterTodosFechamentos();
    }

    @PostMapping("/aberto/mesas")
    public ResponseEntity<?> criarMesa(@RequestBody Mesa novaMesa) {
        try {
            Mesa mesaCriada = fechamentoService.criarMesa(novaMesa);
            return ResponseEntity.ok(mesaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }






    @GetMapping("/aberto/mesas")
    public ResponseEntity<List<Mesa>> obterMesasDoFechamentoAberto() {
        try {
            List<Mesa> mesas =fechamentoService.obterMesasDoFechamentoAberto();
            return ResponseEntity.ok(mesas);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(null);
        }
    }


}
