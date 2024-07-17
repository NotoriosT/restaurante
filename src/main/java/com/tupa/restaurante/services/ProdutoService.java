package com.tupa.restaurante.services;

import com.tupa.restaurante.entidades.Produto;
import com.tupa.restaurante.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto findById(String id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void deleteById(String id) {
        produtoRepository.deleteById(id);
    }

    // Outros métodos de serviço, conforme necessário
}
