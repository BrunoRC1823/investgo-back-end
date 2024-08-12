package com.proyecto.investgo.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.dto.UsuarioDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.entity.Rol;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.repository.UsuarioRepository;
import com.proyecto.investgo.app.service.UsuarioService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private IGeneratorCodigoService codGenService;

	@Autowired
	private IValidationEntityService valService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(Usuario usuario) {

		Usuario usuarioNew = usuarioRepo.save(usuario);
		if (usuarioNew == null) {
			return false;
		}

		return true;
	}

	@Override
	@Transactional
	public Boolean saveAdmin(Usuario usuario) {
		return save(asignarValoresSave(usuario));
	}

	@Override
	@Transactional
	public Boolean updateAdmin(Usuario usuario) {
		return save(asignarValoresUpdate(usuario));
	}

	@Override
	@Transactional
	public Boolean saveUser(Usuario usuario) {
		Usuario usuNew = asignarValoresSave(usuario);
		usuNew.setRol(new Rol(DefaultValues.DEFAULT_ID_ROL));
		return save(usuNew);
	}

	@Override
	@Transactional
	public Boolean updateUser(Usuario usuario) {
		Usuario usuNew = asignarValoresUpdate(usuario);
		usuario.getAuditoria().setEnable(true);
		usuNew.setRol(new Rol(DefaultValues.DEFAULT_ID_ROL));
		return save(usuNew);
	}

	@Override
	@Transactional
	public Boolean deshabilitarUser(Usuario usuario) {
		usuario.getAuditoria().setEnable(false);
		return save(usuario);
	}

	@Override
	@Transactional
	public Boolean updateUserPassword(Usuario usuario, String passwordNueva) {
		if(passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
			return false;
		}
		usuario.setPassword(passwordEncoder.encode(passwordNueva));
		return save(usuario);
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		Optional<Usuario> usuAct = findById(id);
		if (usuAct.isEmpty()) {
			return false;
		}
		usuarioRepo.delete(usuAct.get());
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> findById(String id) {
		return usuarioRepo.findById(id);
	}

	@Override
	public Optional<Usuario> findByCodigo(String codigo) {
		return usuarioRepo.findByCodigo(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> findByUsernameWithRoles(String username) {
		return usuarioRepo.findByUsernameWithRoles(username);
	}
	@Override
	@Transactional(readOnly = true)
	public Optional<UsuarioDTO> findByUsernameWithRolesDto(String username) {
		Optional<Usuario> usuario = usuarioRepo.findByUsernameWithRoles(username);
		if (usuario.isEmpty()) {
			return Optional.empty();
		}
		UsuarioDTO usuarioDto = modelMapper.map(usuario.get(), UsuarioDTO.class);
		return Optional.ofNullable(usuarioDto);
	}
	@Override
	@Transactional(readOnly = true)
	public Optional<UsuarioDTO> findByCodigoDto(String codigo) {
		Optional<Usuario> usuario = usuarioRepo.findByCodigo(codigo);
		if (usuario.isEmpty()) {
			return Optional.empty();
		}
		UsuarioDTO usuarioDto = modelMapper.map(usuario.get(), UsuarioDTO.class);
		return Optional.ofNullable(usuarioDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> findAllWithRoles(Paginador paginador) {
		return usuarioRepo.findAllWithRoles(paginador.getPageable());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UsuarioDTO> findAllByAuditoriaEnable(boolean enable, Paginador paginador) {
		Page<Usuario> usuarios = usuarioRepo.findAllByAuditoriaEnable(enable, paginador.getPageable());
		return usuarios.map(usuario -> modelMapper.map(usuario, UsuarioDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> validarDatosUsuario(Usuario usuario, String id) {
		Map<String, Object> salida = new HashMap<>();
		List<String> existen = new ArrayList<>();
		if (usuarioRepo.existsByUsernameAndIdNot(usuario.getUsername(), id)) {
			existen.add("El username: \'" + usuario.getUsername() + "\' ya existe!");
		}

		if (usuarioRepo.existsByCorreoAndIdNot(usuario.getCorreo(), id)) {
			existen.add("El correo: \'" + usuario.getCorreo() + "\' ya existe!");
		}

		if (usuarioRepo.existsByDniAndIdNot(usuario.getDni(), id)) {
			existen.add("El DNI : \'" + usuario.getDni() + "\' ya existe");
		}
		if (existen.isEmpty()) {
			return salida;
		}
		salida.put("mensaje", existen);
		return salida;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> validarTodo(Usuario usuario, BindingResult result) {
		Map<String, Object> errores = new HashMap<>();
		Optional<Usuario> usuOp = findByCodigo(usuario.getCodigo());
		String valorId = usuOp.isPresent() ? usuOp.get().getId() : "";
		if (result.hasErrors()) {
			errores = valService.validar(result);
		} else {
			errores = validarDatosUsuario(usuario, valorId);
		}
		return errores;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean validarPropietarioUsuario(String id, String codigo) {
		Optional<Usuario> usuActOp = usuarioRepo.findById(id);
		return codigo.equals(usuActOp.get().getCodigo());
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean validarPassword(String id, String password) {
		Optional<Usuario> usuAct = findById(id);
		return passwordEncoder.matches(password, usuAct.get().getPassword());
	}

	@Override
	public Usuario asignarValoresSave(Usuario usuario) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_USUARIO.getPrefix(),
				(int) usuarioRepo.count());
		modelMapper.map(identificador, usuario);
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.addCartera(new Cartera());
		return usuario;
	}

	@Override
	@Transactional
	public Usuario asignarValoresUpdate(Usuario usuario) {
		Optional<Usuario> usuOp = findByCodigo(usuario.getCodigo());
		Usuario usuAct = usuOp.get();
		usuario.setId(usuAct.getId());
		usuario.setCodigo(usuAct.getCodigo());
		usuario.getAuditoria().setFecha(usuAct.getAuditoria().getFecha());
		usuario.setPassword(usuAct.getPassword());
		usuario.setCartera(usuAct.getCartera());
		return usuario;
	}

}
