package com.proyecto.investgo.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.repository.UsuarioRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.investgo.app.auth.SimpleGrantedAuthorityMixin;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements IJWTService {

    public static final SecretKey SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    protected final Log logger = LogFactory.getLog(this.getClass());

    public static final long EXPIRATION_DATE = 3600000L * 4L;

    public static final String PREFIX_TOKEN = "Bearer ";

    public static final String HEADER_STRING = "Authorization";
    @Autowired
    private UsuarioRepository usuRepo;

    @Override
    public String createToken(Authentication auth) throws IOException {
        String username = auth.getName();
        Optional<Usuario> usuOp = usuRepo.findByUsernameWithRoles(username);
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        Claims claims = Jwts.claims();
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        claims.put("idU", usuOp.get().getId());
        return Jwts.builder().setClaims(claims).setSubject(username).signWith(SECRET).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE)).compact();
    }

    @Override
    public String refreshToken(Authentication auth) throws IOException {
        return null;
    }

    @Override
    public boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET.getEncoded()).build().parseClaimsJws(resolve(token)).getBody();
    }

    @Override
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
        Object roles = getClaims(token).get("authorities");
        return Arrays
                .asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
                        .readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
    }

    @Override
    public String resolve(String token) {
        if (token != null && token.startsWith("Bearer")) {
            return token.replace(PREFIX_TOKEN, "");
        }
        return null;
    }

}
