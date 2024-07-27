package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Colaborador;
import com.tupa.restaurante.services.ColaboradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorController {

    @Autowired
    private ColaboradorService colaboradorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Colaborador> createColaborador(@Valid @RequestBody Colaborador colaborador) {
        // Criptografar a senha antes de salvar
        colaborador.setSenha(passwordEncoder.encode(colaborador.getSenha()));
        Colaborador newColaborador = colaboradorService.saveColaborador(colaborador);
        return ResponseEntity.ok(newColaborador);
    }

    @GetMapping
    public ResponseEntity<List<Colaborador>> getAllColaboradores() {
        List<Colaborador> colaboradores = colaboradorService.getAllColaboradores();
        return ResponseEntity.ok(colaboradores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colaborador> getColaboradorById(@PathVariable String id) {
        Colaborador colaborador = colaboradorService.getColaboradorById(id);
        if (colaborador != null) {
            return ResponseEntity.ok(colaborador);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColaborador(@PathVariable String id) {
        colaboradorService.deleteColaborador(id);
        return ResponseEntity.noContent().build();
    }
}
