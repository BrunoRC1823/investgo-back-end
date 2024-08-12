package com.proyecto.investgo.app.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import com.proyecto.investgo.app.dto.UsuarioDTO;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.utils.Paginador;

public interface UsuarioService {
	Boolean delete(String id);

	Boolean save(Usuario usuario);

	Boolean saveUser(Usuario usuario);

	Boolean updateUser(Usuario usuario);

	Boolean deshabilitarUser(Usuario usuario);

	Boolean updateUserPassword(Usuario usuario, String passwordNueva);

	Boolean saveAdmin(Usuario usuario);

	Boolean updateAdmin(Usuario usuario);

	Optional<Usuario> findByUsernameWithRoles(String username);
	Optional<UsuarioDTO> findByUsernameWithRolesDto(String username);

	Optional<Usuario> findByCodigo(String id);

	Optional<Usuario> findById(String id);

	Page<Usuario> findAllWithRoles(Paginador paginador);

	Page<UsuarioDTO> findAllByAuditoriaEnable(boolean enable, Paginador paginador);

	Optional<UsuarioDTO> findByCodigoDto(String codigo);

	Map<String, Object> validarDatosUsuario(Usuario usuario, String id);

	Map<String, Object> validarTodo(Usuario usuario, BindingResult result);

	Boolean validarPropietarioUsuario(String id, String codigo);

	Boolean validarPassword(String id, String password);

	Usuario asignarValoresSave(Usuario usuario);

	Usuario asignarValoresUpdate(Usuario usuario);
}
