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

    public record SaludoRequest(String nombre) { }

    // 1. Endpoint Público (Sin validación)
    @GetMapping("/public")
    public Map<String, String> endpointPublico() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Endpoint libre - Acceso permitido sin validacion de token");
        return response;
    }

    // 2. Endpoint GET requerido por la nueva guía (Retorna la cadena con todos los claims de Cognito)
    @GetMapping("/status")
    public String saludo(@AuthenticationPrincipal Jwt jwt) {
        var usuario = jwt.getClaims();
        return "hola mundo - correccion bug v1.1.1 usuario autenticado: " + usuario;
    }

    // 3. Endpoint POST (Conserva la lógica del Body e incluye los datos del token)
    @PostMapping("/saludo")
    public Map<String, Object> saludar(@RequestBody SaludoRequest request, @AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();

        if (request == null || request.nombre() == null || request.nombre().trim().isEmpty()) {
            response.put("error", "El campo 'nombre' es obligatorio y no puede estar vacio.");
            return response;
        }

        response.put("mensaje", "Hola " + request.nombre());
        response.put("usuarioSub", jwt.getSubject());
        response.put("cognitoClaims", jwt.getClaims()); // Muestra todos los claims emitidos por Cognito
        return response;
    }
}