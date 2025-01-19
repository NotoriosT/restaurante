package com.tupa.restaurante.controller;

import com.tupa.restaurante.entities.Mesa;
import com.tupa.restaurante.service.MesaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping("/{mesaId}")
    public ResponseEntity<Mesa> obterMesa(@PathVariable String mesaId) {
        Mesa mesa = mesaService.obterMesaPorId(mesaId);
        return ResponseEntity.ok(mesa);
    }

    @PostMapping
    public ResponseEntity<Mesa> criarMesa(@RequestBody Mesa novaMesa) {
        Mesa mesaCriada = mesaService.criarMesa(novaMesa);
        return ResponseEntity.ok(mesaCriada);
    }

    @GetMapping
    public ResponseEntity<List<Mesa>> listarMesas() {
        List<Mesa> mesas = mesaService.listarMesas();
        return ResponseEntity.ok(mesas);
    }

    @PutMapping("/{mesaId}")
    public ResponseEntity<Mesa> atualizarMesa(@PathVariable String mesaId, @RequestBody Mesa mesaAtualizada) {
        Mesa mesa = mesaService.atualizarMesa(mesaId, mesaAtualizada);
        return ResponseEntity.ok(mesa);
    }

    @DeleteMapping("/{mesaId}")
    public ResponseEntity<Void> deletarMesa(@PathVariable String mesaId) {
        mesaService.deletarMesa(mesaId);
        return ResponseEntity.noContent().build();
    }
}
