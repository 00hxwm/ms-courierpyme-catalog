package com.courierpyme.config;

import com.courierpyme.model.ServicioEnvio;
import com.courierpyme.repository.ServicioEnvioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ServicioEnvioRepository repository;

    public DataInitializer(ServicioEnvioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(crear("Express 24h", "Entrega el mismo día o al siguiente día hábil", "4990", 50));
        repository.save(crear("Estándar 48h", "Entrega en 2 días hábiles", "2990", 120));
        repository.save(crear("Económico 72h", "Entrega en 3 días hábiles", "1990", 200));
    }

    private ServicioEnvio crear(String nombre, String descripcion, String tarifa, int capacidad) {
        ServicioEnvio s = new ServicioEnvio();
        s.setNombre(nombre);
        s.setDescripcion(descripcion);
        s.setTarifa(new BigDecimal(tarifa));
        s.setCapacidadDisponible(capacidad);
        s.setActivo(true);
        return s;
    }
}