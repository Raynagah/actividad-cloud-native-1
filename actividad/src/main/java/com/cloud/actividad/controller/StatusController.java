package com.cloud.actividad.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class StatusController {

    public record SaludoRequest(String nombre) {

    }

    // 1. Endpoint Público (No requiere Token JWT)
    @GetMapping("/public")
    public Map<String, String> endpointPublico() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Endpoint libre - Acceso permitido sin validacion de token");
        return response;
    }

    // 2. Endpoint Protegido GET (Valida el Token y extrae el usuario)
    @GetMapping("/status")
    public Map<String, String> getStatus(@AuthenticationPrincipal Jwt jwt) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("mensaje", "Microservicio activo y funcionando");
        response.put("usuarioAutenticado", jwt.getSubject()); // Extrae 'sub' del JWT
        return response;
    }

    // 3. Endpoint Protegido POST (Valida el Token y la lógica de negocio)
    @PostMapping("/saludo")
    public Map<String, String> saludar(@RequestBody SaludoRequest request, @AuthenticationPrincipal Jwt jwt) {
        Map<String, String> response = new HashMap<>();

        if (request == null || request.nombre() == null || request.nombre().trim().isEmpty()) {
            response.put("error", "El campo 'nombre' es obligatorio y no puede estar vacio.");
            return response;
        }

        response.put("mensaje", "Hola " + request.nombre());
        response.put("usuarioAutenticado", jwt.getSubject()); // Extrae 'sub' del JWT
        return response;
    }
}
