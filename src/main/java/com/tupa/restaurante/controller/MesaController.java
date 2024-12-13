package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Mesa;
import com.tupa.restaurante.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping("/{mesaId}")
    public Mesa obterMesa(@PathVariable String mesaId) {
        return mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada"));
    }
}
