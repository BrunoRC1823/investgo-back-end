package com.proyecto.investgo.app.controller;

import com.proyecto.investgo.app.auth.service.IJWTService;
import com.proyecto.investgo.app.auth.service.JWTServiceImpl;
import com.proyecto.investgo.app.dto.UsuarioDTO;
import com.proyecto.investgo.app.service.UsuarioService;
import com.proyecto.investgo.app.utils.DefaultValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class AuthController {
    @Autowired
    private IJWTService jwtService;
    @Autowired
    private UsuarioService usuService;

    @GetMapping("/v1/check-token")
    public ResponseEntity<?> refreshToken(Authentication authResult) throws IOException {
        String token = jwtService.createToken(authResult);
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        Optional<UsuarioDTO> opUsu = usuService.findByUsernameWithRolesDto( authResult.getName() );
        body.put("user", opUsu.get());
        return ResponseEntity.ok(body);
    }
}
