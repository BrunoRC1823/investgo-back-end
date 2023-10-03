package com.proyecto.investgo.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.investgo.app.auth.service.IJWTService;
import com.proyecto.investgo.app.auth.service.JWTServiceImpl;
import com.proyecto.investgo.app.entity.Usuario;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private IJWTService jwtService;

	private AuthenticationManager authenticationManager;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, IJWTService jwtService) {
		this.authenticationManager = authenticationManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/v1/auth/login", "POST"));
		this.jwtService = jwtService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		boolean usernameNotNull = (username != null);
		boolean passwordNotNull = (password != null);

		if (usernameNotNull && passwordNotNull) {
			logger.info("Username(form-data): " + username);
			logger.info("Password(form-data): " + password);
		} else {
			Usuario usuario = null;

			try {
				usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
				username = usuario.getUsername();
				password = usuario.getPassword();
				logger.info("Username(raw): " + username);
				logger.info("Password(raw): " + password);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String token = jwtService.createToken(authResult);
		response.addHeader(JWTServiceImpl.HEADER_STRING, JWTServiceImpl.PREFIX_TOKEN + token);
		Map<String, Object> body = new HashMap<>();
		body.put("token", token);
		body.put("user", authResult.getPrincipal());
		body.put("mensaje", String.format("Hola %s, has iniciado sesion con exito!", authResult.getName()));
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(200);
		response.setContentType("application/json");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		Map<String, Object> body = new HashMap<>();
		body.put("mensaje", "Error de autenticacion: Username o password incorrecto!");
		body.put("error", failed.getMessage());

		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setStatus(401);
		response.setContentType("application/json");
	}

}
