package com.courierpyme.repository;

import com.courierpyme.model.ServicioEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicioEnvioRepository extends JpaRepository<ServicioEnvio, Long> {
    Optional<ServicioEnvio> findByNombreIgnoreCase(String nombre);
}