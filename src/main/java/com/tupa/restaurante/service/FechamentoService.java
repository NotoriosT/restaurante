package com.tupa.restaurante.service;

import com.tupa.restaurante.entities.Fechamento;
import com.tupa.restaurante.entities.Mesa;
import com.tupa.restaurante.entities.enums.StatusFechamento;
import com.tupa.restaurante.repository.FechamentoRepository;
import com.tupa.restaurante.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FechamentoService {

    @Autowired
    private FechamentoRepository fechamentoRepository;
    @Autowired
    private MesaRepository mesaRepository;

    public Fechamento abrirFechamento() {
        Optional<Fechamento> fechamentoAberto = fechamentoRepository.findByStatus(StatusFechamento.ABERTO);
        if (fechamentoAberto.isPresent()) {
            throw new IllegalStateException("Já existe um fechamento aberto.");
        }

        Fechamento novoFechamento = new Fechamento();
        novoFechamento.setDataAbertura(LocalDateTime.now());
        novoFechamento.setStatus(StatusFechamento.ABERTO);
        return fechamentoRepository.save(novoFechamento);
    }

    public Fechamento fecharFechamento() {
        Optional<Fechamento> fechamentoAberto = fechamentoRepository.findByStatus(StatusFechamento.ABERTO);
        if (fechamentoAberto.isEmpty()) {
            throw new IllegalStateException("Nenhum fechamento aberto no momento.");
        }

        Fechamento fechamento = fechamentoAberto.get();
        fechamento.setDataFechamento(LocalDateTime.now());
        fechamento.setStatus(StatusFechamento.FECHADO);
        return fechamentoRepository.save(fechamento);
    }

    public Optional<Fechamento> obterFechamentoAberto() {
        return fechamentoRepository.findByStatus(StatusFechamento.ABERTO);
    }

    public List<Fechamento> obterTodosFechamentos() {
        return fechamentoRepository.findAll();
    }



    public Mesa criarMesa(Mesa novaMesa) {
        Fechamento fechamentoAberto = obterFechamentoAberto()
                .orElseThrow(() -> new IllegalStateException("Nenhum fechamento aberto no momento."));

        // Verificar se já existe uma mesa com o mesmo número no fechamento atual

        boolean mesaExistente = mesaRepository.findAll().stream()
                .anyMatch(mesa -> mesa.getIdFechamento().equals(fechamentoAberto.getId())
                        && mesa.getNumero() == novaMesa.getNumero());

        if (mesaExistente) {
            throw new IllegalArgumentException("Já existe uma mesa com o número " + novaMesa.getNumero() + " neste fechamento.");
        }

        // Configurar o ID do fechamento na mesa e salvar
        novaMesa.setIdFechamento(fechamentoAberto.getId());
        return mesaRepository.save(novaMesa);
    }

    public List<Mesa> obterMesasDoFechamentoAberto() {
        // Obtém o fechamento aberto
        Fechamento fechamentoAberto = fechamentoRepository.findByStatus(StatusFechamento.ABERTO)
                .orElseThrow(() -> new IllegalStateException("Nenhum fechamento aberto."));

        // Retorna as mesas associadas ao fechamento aberto
        return mesaRepository.findByIdFechamento(fechamentoAberto.getId());
    }
}
