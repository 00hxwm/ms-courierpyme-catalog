package com.courierpyme.dto;

import java.math.BigDecimal;

public record ServicioRequest(
        String nombre,
        String descripcion,
        BigDecimal tarifa,
        Integer capacidadDisponible,
        Boolean activo) {
}