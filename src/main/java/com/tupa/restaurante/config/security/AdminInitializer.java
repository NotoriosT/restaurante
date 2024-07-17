package com.tupa.restaurante.config.security;

import com.tupa.restaurante.entidades.Colaborador;
import com.tupa.restaurante.entidades.TipoFuncionario;
import com.tupa.restaurante.repository.ColaboradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Configuration
public class AdminInitializer {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Optional<Colaborador> admin = colaboradorRepository.findByLogin("admin");
        if (admin.isEmpty()) {
            Colaborador newAdmin = new Colaborador();
            newAdmin.setLogin("admin");
            newAdmin.setSenha(passwordEncoder.encode("1234"));
            newAdmin.setTipoFuncionario(TipoFuncionario.ADMIN);
            colaboradorRepository.save(newAdmin);
            System.out.println("Admin user created with login: admin and password: 1234");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
