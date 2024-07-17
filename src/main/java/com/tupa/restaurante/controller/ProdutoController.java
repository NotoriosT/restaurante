package com.tupa.restaurante.controller;

import com.tupa.restaurante.entidades.Produto;
import com.tupa.restaurante.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    @PostMapping
    public Produto createProduto(@RequestPart("produto") Produto produto, @RequestPart("file") MultipartFile file) throws IOException {
        produto.setImagem(Base64.getEncoder().encodeToString(file.getBytes()));
        return produtoRepository.save(produto);
    }
}
