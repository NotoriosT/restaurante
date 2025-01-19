// src/main/java/com/tupa/restaurante/dto/DatasetDTO.java
package com.tupa.restaurante.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DatasetDTO {
    private String label;
    private List<Number> data;
    private List<String> backgroundColor;
}
