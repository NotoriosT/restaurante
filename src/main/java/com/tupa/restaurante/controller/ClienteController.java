package com.tupa.restaurante.controller;

import com.tupa.restaurante.entities.Cliente;
import com.tupa.restaurante.service.ClienteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteServices clienteServices;

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        clienteServices.saveCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }
    @GetMapping
    public ResponseEntity<List<Cliente>> listResponseEntityCliente() {
        return ResponseEntity.ok(clienteServices.getClientes());
    }
}
