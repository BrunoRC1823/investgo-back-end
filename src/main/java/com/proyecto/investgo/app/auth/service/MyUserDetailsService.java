package com.proyecto.investgo.app.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.repository.UsuarioRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UsuarioRepository usuRepo;

	private Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Usuario> oUsuario = usuRepo.findByUsernameWithRoles(username);
		if (oUsuario.isEmpty()) {
			log.info("Error en el login: No exite el usuario con username: " + username);
			throw new UsernameNotFoundException("Credenciales invalidas!");
		}
		Usuario usuario = oUsuario.get();
		if (usuario == null || !usuario.getUsername().equals(username)) {
			log.info("Error en el login: No exite el usuario con username: " + username);
			throw new UsernameNotFoundException("Credenciales invalidas!");
		}
		List<GrantedAuthority> auth = new ArrayList<>();
		auth.add(new SimpleGrantedAuthority(usuario.getRol().getCodigo()));
		if (auth.isEmpty()) {
			log.info("Error en el login: El usuario con username: " + username + " no tiene roles asignados!");
			throw new UsernameNotFoundException("No tiene roles asignados!");
		}
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		session.setAttribute("usuarioSession", usuario);
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getAuditoria().getEnable(), true, true,
				true, auth);
	}
}
