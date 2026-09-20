package com.courierpyme.controller;

import com.courierpyme.dto.ServicioRequest;
import com.courierpyme.model.ServicioEnvio;
import com.courierpyme.repository.ServicioEnvioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalog/services")
public class ServicioEnvioController {

    private final ServicioEnvioRepository repository;

    public ServicioEnvioController(ServicioEnvioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public ResponseEntity<List<ServicioEnvio>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Admin','Operador')")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<ServicioEnvio> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return noEncontrado();
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> crear(@RequestBody ServicioRequest req) {
        String error = validar(req);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        if (repository.findByNombreIgnoreCase(req.nombre().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un servicio con ese nombre"));
        }

        ServicioEnvio servicio = new ServicioEnvio();
        aplicar(servicio, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(servicio));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ServicioRequest req) {
        Optional<ServicioEnvio> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return noEncontrado();
        }

        String error = validar(req);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        boolean nombreEnUso = repository.findByNombreIgnoreCase(req.nombre().trim())
                .filter(otro -> !otro.getId().equals(id))
                .isPresent();
        if (nombreEnUso) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya existe un servicio con ese nombre"));
        }

        ServicioEnvio servicio = opt.get();
        aplicar(servicio, req);
        return ResponseEntity.ok(repository.save(servicio));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return noEncontrado();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String validar(ServicioRequest req) {
        if (req == null || req.nombre() == null || req.nombre().isBlank()) {
            return "El nombre es obligatorio";
        }
        if (req.nombre().trim().length() > 100) {
            return "El nombre no puede superar los 100 caracteres";
        }
        if (req.descripcion() != null && req.descripcion().length() > 255) {
            return "La descripción no puede superar los 255 caracteres";
        }
        if (req.tarifa() == null || req.tarifa().signum() < 0) {
            return "La tarifa es obligatoria y no puede ser negativa";
        }
        if (req.capacidadDisponible() == null || req.capacidadDisponible() < 0) {
            return "La capacidad es obligatoria y no puede ser negativa";
        }
        return null;
    }

    private void aplicar(ServicioEnvio servicio, ServicioRequest req) {
        servicio.setNombre(req.nombre().trim());
        servicio.setDescripcion(req.descripcion() == null ? null : req.descripcion().trim());
        servicio.setTarifa(req.tarifa());
        servicio.setCapacidadDisponible(req.capacidadDisponible());
        if (req.activo() != null) {
            servicio.setActivo(req.activo());
        }
    }

    private ResponseEntity<?> noEncontrado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Servicio no encontrado"));
    }
}