package com.tupa.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MenuItemDTO {

    private String name;
    private String icon;
    private List<String> roles;

    public MenuItemDTO() {
    }

    public MenuItemDTO(String name, String icon, List<String> roles) {
        this.name = name;
        this.icon = icon;
        this.roles = roles;
    }

}
