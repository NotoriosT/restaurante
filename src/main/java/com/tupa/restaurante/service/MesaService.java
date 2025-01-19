
package com.tupa.restaurante.service;

import com.mongodb.DuplicateKeyException;
import com.tupa.restaurante.entities.Mesa;
import com.tupa.restaurante.exceptions.MesaAlreadyExistsException;
import com.tupa.restaurante.exceptions.MesaNotFoundException;
import com.tupa.restaurante.repository.MesaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MesaService {

    private static final Logger logger = LoggerFactory.getLogger(MesaService.class);

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    /**
     * Cria uma nova mesa. Aplica a validação do índice único (numero + idFechamento).
     *
     * @param novaMesa A nova mesa a ser criada.
     * @return A mesa criada.
     * @throws MesaAlreadyExistsException se já existir uma mesa com o mesmo número e idFechamento.
     */
    @Transactional
    public Mesa criarMesa(Mesa novaMesa) {
        logger.info("Tentando criar uma nova mesa: {}", novaMesa);
        try {
            Mesa mesaCriada = mesaRepository.save(novaMesa);
            logger.info("Mesa criada com sucesso: {}", mesaCriada);
            return mesaCriada;
        } catch (DuplicateKeyException e) {
            logger.error("Erro ao criar mesa: Mesa já existente com número {} e idFechamento {}",
                    novaMesa.getNumero(), novaMesa.getIdFechamento());
            throw new MesaAlreadyExistsException("Já existe uma mesa com este número no fechamento especificado.");
        }
    }

    /**
     * Obtém uma mesa pelo seu ID.
     *
     * @param mesaId O ID da mesa.
     * @return A mesa encontrada.
     * @throws MesaNotFoundException se a mesa não for encontrada.
     */
    public Mesa obterMesaPorId(String mesaId) {
        logger.info("Obtendo mesa com ID: {}", mesaId);
        return mesaRepository.findById(mesaId)
                .orElseThrow(() -> {
                    logger.warn("Mesa não encontrada com ID: {}", mesaId);
                    return new MesaNotFoundException("Mesa não encontrada com ID: " + mesaId);
                });
    }

    /**
     * Lista todas as mesas.
     *
     * @return Lista de todas as mesas.
     */
    public List<Mesa> listarMesas() {
        logger.info("Listando todas as mesas.");
        List<Mesa> mesas = mesaRepository.findAll();
        logger.info("Total de mesas encontradas: {}", mesas.size());
        return mesas;
    }

    /**
     * Atualiza os dados de uma mesa existente.
     *
     * @param mesaId          O ID da mesa a ser atualizada.
     * @param mesaAtualizada  Os novos dados da mesa.
     * @return A mesa atualizada.
     * @throws MesaAlreadyExistsException se a atualização resultar em um conflito de chave única.
     */
    @Transactional
    public Mesa atualizarMesa(String mesaId, Mesa mesaAtualizada) {
        logger.info("Atualizando mesa com ID: {}", mesaId);
        Mesa mesaExistente = obterMesaPorId(mesaId);

        boolean atualizou = false;

        if (mesaAtualizada.getNumero() > 0 && mesaAtualizada.getNumero() != mesaExistente.getNumero()) {
            mesaExistente.setNumero(mesaAtualizada.getNumero());
            atualizou = true;
        }
        if (mesaAtualizada.getStatus() != null && !mesaAtualizada.getStatus().equals(mesaExistente.getStatus())) {
            mesaExistente.setStatus(mesaAtualizada.getStatus());
            atualizou = true;
        }
        if (mesaAtualizada.getIdFechamento() != null && !mesaAtualizada.getIdFechamento().isBlank()
                && !mesaAtualizada.getIdFechamento().equals(mesaExistente.getIdFechamento())) {
            mesaExistente.setIdFechamento(mesaAtualizada.getIdFechamento());
            atualizou = true;
        }

        if (atualizou) {
            try {
                Mesa mesaSalva = mesaRepository.save(mesaExistente);
                logger.info("Mesa atualizada com sucesso: {}", mesaSalva);
                return mesaSalva;
            } catch (DuplicateKeyException e) {
                logger.error("Erro ao atualizar mesa: Conflito de chave única para número {} e idFechamento {}",
                        mesaAtualizada.getNumero(), mesaAtualizada.getIdFechamento());
                throw new MesaAlreadyExistsException("Já existe uma mesa com este número no fechamento especificado.");
            }
        } else {
            logger.info("Nenhuma atualização necessária para a mesa com ID: {}", mesaId);
            return mesaExistente;
        }
    }

    /**
     * Exclui uma mesa pelo ID, caso necessário.
     *
     * @param mesaId O ID da mesa a ser excluída.
     * @throws MesaNotFoundException se a mesa não for encontrada.
     */
    @Transactional
    public void deletarMesa(String mesaId) {
        logger.info("Tentando deletar mesa com ID: {}", mesaId);
        Mesa mesa = obterMesaPorId(mesaId);
        mesaRepository.delete(mesa);
        logger.info("Mesa deletada com sucesso: {}", mesaId);
    }
}
