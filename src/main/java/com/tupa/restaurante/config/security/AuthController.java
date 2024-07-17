package com.tupa.restaurante.config.security;


import com.tupa.restaurante.entidades.Colaborador;
import com.tupa.restaurante.entidades.LoginRequestDTO;
import com.tupa.restaurante.entidades.RegisterRequestDTO;
import com.tupa.restaurante.entidades.ResponseDTO;
import com.tupa.restaurante.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ColaboradorRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        Colaborador colaborador = repository.findByLogin(body.login())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(passwordEncoder.matches(body.senha(), colaborador.getSenha())) {
            String token = tokenService.generateToken(colaborador);
            return ResponseEntity.ok(new ResponseDTO(colaborador.getLogin(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<Colaborador> colaborador = repository.findByLogin(body.login());

        if(colaborador.isEmpty()) {
            Colaborador newColaborador = new Colaborador();
            newColaborador.setSenha(passwordEncoder.encode(body.senha()));
            newColaborador.setLogin(body.login());
            repository.save(newColaborador);

            String token = tokenService.generateToken(newColaborador);
            return ResponseEntity.ok(new ResponseDTO(newColaborador.getLogin(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
