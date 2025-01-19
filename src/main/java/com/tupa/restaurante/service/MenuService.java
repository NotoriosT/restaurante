package com.tupa.restaurante.service;

import com.tupa.restaurante.dto.MenuItemDTO;
import com.tupa.restaurante.entities.menu.MenuItem;
import com.tupa.restaurante.exceptions.MenuNotFoundException;
import com.tupa.restaurante.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Retorna uma lista de MenuItemDTO com base nas roles fornecidas.
     *
     * @param roles Lista de roles do usuário
     * @return Lista de MenuItemDTO
     */
    public List<MenuItemDTO> getMenuByRoles(List<String> roles) {
        // Converter as roles para remover o prefixo "ROLE_" se necessário
        List<String> normalizedRoles = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toList());

        for (String role : normalizedRoles) {
            System.out.println(role);
        }
        List<MenuItem> menuItems = menuItemRepository.findByRolesIn(normalizedRoles);
        if (menuItems.isEmpty()) {
            throw new MenuNotFoundException("Nenhum menu encontrado para as roles fornecidas.");
        }

        return menuItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo item de menu.
     *
     * @param menuItemDTO DTO do item de menu a ser criado
     * @return DTO do item de menu criado
     */
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = convertToEntity(menuItemDTO);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToDTO(savedMenuItem);
    }

    /**
     * Converte uma entidade MenuItem para MenuItemDTO.
     *
     * @param menuItem Entidade MenuItem
     * @return DTO correspondente
     */
    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        return new MenuItemDTO(menuItem.getName(), menuItem.getIcon(), menuItem.getRoles());
    }



    /**
     * Converte um MenuItemDTO para entidade MenuItem.
     *
     * @param menuItemDTO DTO do MenuItem
     * @return Entidade MenuItem
     */
    private MenuItem convertToEntity(MenuItemDTO menuItemDTO) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setIcon(menuItemDTO.getIcon());
        menuItem.setRoles(menuItemDTO.getRoles());

        // Definindo as roles associadas ao MenuItem
        // Supondo que MenuItemDTO tenha um campo 'roles' (lista de strings)
        // Caso contrário, você pode definir as roles diretamente aqui ou ajustar conforme necessário
        // Exemplo:
        // menuItem.setRoles(menuItemDTO.getRoles());

        return menuItem;
    }
}
