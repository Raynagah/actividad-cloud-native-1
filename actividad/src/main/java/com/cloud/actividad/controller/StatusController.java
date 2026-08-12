package com.cloud.actividad.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

    public record SaludoRequest(String nombre) {}

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("mensaje", "Microservicio activo y funcionando");
        return response;
    }

    @PostMapping("/saludo")
    public Map<String, String> saludar(@RequestBody SaludoRequest request) {
        Map<String, String> response = new HashMap<>();

        if (request == null || request.nombre() == null || request.nombre().trim().isEmpty()) {
            response.put("error", "El campo 'nombre' es obligatorio y no puede estar vacio.");
            return response;
        }

        response.put("mensaje", "Hola " + request.nombre());
        return response;
    }
}