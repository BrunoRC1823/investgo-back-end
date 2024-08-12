package com.proyecto.investgo.app.utils.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.service.IDecoderTokenService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@Service
public class DecoderTokenServiceImpl implements IDecoderTokenService {
    @Override
    public Map<String, Object> decodeToken(String token){
        // Remover el prefijo "Bearer " si existe
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Dividir el token en sus partes
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token inválido");
        }

        // Decodificar el payload del token (la segunda parte)
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Convertir el JSON del payload a un mapa de claims
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> claims;
        try {
            claims = objectMapper.readValue(payloadJson, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo decodificar el token", e);
        }

        return claims;
    }

    @Override
    public String getIdUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> claims = decodeToken(token);
        return claims.get(DefaultValues.CLAIM_ID).toString();
    }
}
