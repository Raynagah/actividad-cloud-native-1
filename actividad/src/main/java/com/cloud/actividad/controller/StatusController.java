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

    @GetMapping("/public")
    public Map<String, String> endpointPublico() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Endpoint libre - Acceso permitido sin validacion de token");
        return response;
    }

    @GetMapping("/status")
    public String saludo(@AuthenticationPrincipal Jwt jwt) {
        var usuario = jwt.getClaims();
        return "hola mundo - autenticado con Azure AD: " + usuario;
    }

    @PostMapping("/saludo")
    public Map<String, Object> saludar(@RequestBody SaludoRequest request, @AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();

        if (request == null || request.nombre() == null || request.nombre().trim().isEmpty()) {
            response.put("error", "El campo 'nombre' es obligatorio y no puede estar vacio.");
            return response;
        }

        response.put("mensaje", "Hola " + request.nombre());
        response.put("usuarioSub", jwt.getSubject());
        response.put("azureClaims", jwt.getClaims());
        return response;
    }
}