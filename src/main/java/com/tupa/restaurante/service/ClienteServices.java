package com.tupa.restaurante.service;

import com.tupa.restaurante.entities.Cliente;
import com.tupa.restaurante.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServices {
    @Autowired
    private ClienteRepository clienteRepository;
    @Transactional
    public String saveCliente(Cliente cliente) {
        clienteRepository.save(cliente);
        return cliente.getId();
    }

    public Cliente getClienteById(String id) {
        return clienteRepository.findById(id).orElse(null);
    }
    @Transactional
    public void deleteCliente(String id) {
        clienteRepository.deleteById(id);
    }
    public List<Cliente> getClientes(){
        return clienteRepository.findAll();
    }

}
