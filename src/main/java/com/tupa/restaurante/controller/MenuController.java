package com.tupa.restaurante.controller;

import com.tupa.restaurante.dto.MenuItemDTO;
import com.tupa.restaurante.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Cria um novo item de menu.
     *
     * @param menuItemDTO DTO do item de menu a ser criado
     * @return ResponseEntity com o MenuItemDTO criado
     */
    @PostMapping
    public ResponseEntity<MenuItemDTO> createMenuItem(@RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO createdMenuItem = menuService.createMenuItem(menuItemDTO);
        return ResponseEntity.ok(createdMenuItem);
    }

    /**
     * Retorna o menu baseado nas roles do usuário autenticado.
     *
     * @param authentication Objeto Authentication contendo as details do usuário
     * @return ResponseEntity com a lista de MenuItemDTO
     */
    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getMenu(Authentication authentication) {
        // Extrai as roles do usuário a partir do objeto Authentication
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Obtém o menu baseado nas roles
        List<MenuItemDTO> menu = menuService.getMenuByRoles(roles);

        return ResponseEntity.ok(menu);
    }
}
