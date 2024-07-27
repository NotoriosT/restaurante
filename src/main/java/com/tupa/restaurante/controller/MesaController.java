package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Mesa;
import com.tupa.restaurante.repository.MesaRepository;
import com.tupa.restaurante.services.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private static final Logger logger = Logger.getLogger(MesaController.class.getName());

    @Autowired
    private MesaService mesaService;
    @Autowired
    private MesaRepository mesaRepository;
    @GetMapping
    public List<Mesa> getAllMesas() {
       return  mesaRepository.findAll();
    }

    @PostMapping
    public Mesa createMesa(@Valid @RequestBody Mesa mesa) {
        logUserRoles();
        return mesaService.createMesa(mesa);
    }

    @GetMapping("/{id}/total-gasto")
    public double calcularGastoPorMesa(@PathVariable int id) {
        logUserRoles();
        return mesaService.calcularGastoPorMesa(id);
    }

    private void logUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logger.info("User " + authentication.getName() + " has roles: " + authentication.getAuthorities());
        }
    }
}
