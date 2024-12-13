package com.tupa.restaurante.services;

import com.mongodb.DuplicateKeyException;
import com.tupa.restaurante.entidades.Mesa;
import com.tupa.restaurante.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    /**
     * Cria uma nova mesa. Aplica a validação do índice único (numero + idfechamento).
     */
    public Mesa criarMesa(Mesa novaMesa) {
        try {
            return mesaRepository.save(novaMesa);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Já existe uma mesa com este número no fechamento especificado.");
        }
    }

    /**
     * Obtém uma mesa pelo seu ID.
     */
    public Mesa obterMesaPorId(String mesaId) {
        return mesaRepository.findById(mesaId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada com ID: " + mesaId));
    }

    /**
     * Lista todas as mesas.
     */
    public List<Mesa> listarMesas() {
        return mesaRepository.findAll();
    }

    /**
     * Atualiza os dados de uma mesa existente.
     */
    public Mesa atualizarMesa(String mesaId, Mesa mesaAtualizada) {
        Mesa mesaExistente = obterMesaPorId(mesaId);

        // Atualiza os campos conforme necessário
        if (mesaAtualizada.getNumero() > 0) {
            mesaExistente.setNumero(mesaAtualizada.getNumero());
        }
        if (mesaAtualizada.getStatus() != null) {
            mesaExistente.setStatus(mesaAtualizada.getStatus());
        }
        if (mesaAtualizada.getIdfechamento() != null && !mesaAtualizada.getIdfechamento().isBlank()) {
            mesaExistente.setIdfechamento(mesaAtualizada.getIdfechamento());
        }

        return mesaRepository.save(mesaExistente);
    }

    /**
     * Exclui uma mesa pelo ID, caso necessário.
     */
    public void deletarMesa(String mesaId) {
        Mesa mesa = obterMesaPorId(mesaId);
        mesaRepository.delete(mesa);
    }
}
