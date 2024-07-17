package com.tupa.restaurante.services;


import com.tupa.restaurante.entidades.Colaborador;

import com.tupa.restaurante.repository.ColaboradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    public Colaborador saveColaborador(Colaborador colaborador) {
        return colaboradorRepository.save(colaborador);
    }

    public List<Colaborador> getAllColaboradores() {
        return colaboradorRepository.findAll();
    }

    public Colaborador getColaboradorById(String id) {
        return colaboradorRepository.findById(id).orElse(null);
    }

    public void deleteColaborador(String id) {
        colaboradorRepository.deleteById(id);
    }
}
