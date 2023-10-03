package com.proyecto.investgo.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.investgo.app.dto.CarteraDTO;
import com.proyecto.investgo.app.dto.RolDto;
import com.proyecto.investgo.app.dto.UsuarioDTO;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.service.CarteraService;
import com.proyecto.investgo.app.service.RolService;
import com.proyecto.investgo.app.service.UsuarioService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RolService rolService;
	@Autowired
	private CarteraService carteraService;

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/v1/usuarios-user/{codigo}")
	public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo, HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		Boolean esPropietario = usuarioService.validarPropietarioUsuario(usuSession.getId(), codigo);
		if (!esPropietario) {
			salida.put("mensaje", "Ocurrio un error!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(usuarioService.findByCodigoDto(codigo));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/usuarios/{id}")
	public ResponseEntity<?> buscarPorId(@PathVariable String id) {
		Map<String, Object> salida = new HashMap<>();
		Optional<Usuario> usuario = usuarioService.findById(id);
		if (usuario.isEmpty()) {
			salida.put("mensaje", "El usuario no existe!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(usuario.get());
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/usuarios/activos")
	public ResponseEntity<Page<UsuarioDTO>> listarActivos(Paginador paginador) {
		Page<UsuarioDTO> lista = usuarioService.findAllByAuditoriaEnable(true, paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/usuarios/buscar/{enable}")
	public ResponseEntity<Page<UsuarioDTO>> buscarEnable(
			@PathVariable(value = DefaultValues.DEFAULT_ENABLE) String enable, Paginador paginador) {
		boolean valorEnable = enable.equals(DefaultValues.DEFAULT_ENABLE);
		Page<UsuarioDTO> lista = usuarioService.findAllByAuditoriaEnable(valorEnable, paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/usuarios")
	public ResponseEntity<Page<Usuario>> listar(Paginador paginador) {
		Page<Usuario> lista = usuarioService.findAllWithRoles(paginador);
		return ResponseEntity.ok(lista);
	}

	@PostMapping("/v1/sing-up")
	public ResponseEntity<?> signup(@RequestBody @Valid Usuario usuario, BindingResult result,
			HttpServletRequest request) {
		Map<String, Object> salida = new HashMap<>();
		try {
			Map<String, Object> errors = usuarioService.validarTodo(usuario, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean usuarioNew = usuarioService.saveUser(usuario);
			if (!usuarioNew) {
				salida.put("mensaje", "No se pudo registrar su cuenta, contacte con un administrador!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Su cuenta fue creada exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PutMapping("/v1/usuarios-user/password")
	public ResponseEntity<?> actualizarPassword(@RequestBody Map<String, Object> request, BindingResult result,
			HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		String passwordActual = (String) request.get("passwordActual");
		String passwordNuevo = (String) request.get("passwordNuevo");
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		try {
			boolean passIgual = usuarioService.validarPassword(usuSession.getId(), passwordActual);
			if (!passIgual) {
				salida.put("mensaje", "La contraseña no conincide con la contraseña actual!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			boolean usuarioNew = usuarioService.updateUserPassword(usuSession, passwordNuevo);
			if (!usuarioNew) {
				salida.put("mensaje", "No se pudo actualizar tu contraseña, intenalo de nuevo");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Su contraseña fue actualizada exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar la contraseña!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/v1/usuarios-user")
	public ResponseEntity<?> actualizarUsuario(@RequestBody @Valid Usuario usuario, BindingResult result,
			HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		Boolean esPropietario = usuarioService.validarPropietarioUsuario(usuSession.getId(), usuario.getCodigo());
		if (!esPropietario) {
			salida.put("mensaje", "Ocurrio un error!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		try {
			Map<String, Object> errors = usuarioService.validarTodo(usuario, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean usuarioNew = usuarioService.updateUser(usuario);
			if (!usuarioNew) {
				salida.put("mensaje", "No se pudo actualizar tu cuenta, contacta a un administrador!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Su cuenta fue actualizada exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/v1/usuarios-user/deshabilitar")
	public ResponseEntity<?> deshabilitar(HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		try {
			boolean usuarioDes = usuarioService.deshabilitarUser(usuSession);
			if (!usuarioDes) {
				salida.put("mensaje", "No se pudo deshabilitar su cuenta, comuniquese con un administrador!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			session.invalidate();
			salida.put("mensaje", "Cuenta deshabilitada!");
			return ResponseEntity.ok(salida);
		} catch (Exception e) {
			salida.put("mensaje", "Hubo un error al deshabilitar el usuario: " + e.getMessage());
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/v1/usuarios")
	public ResponseEntity<?> guardar(@RequestBody @Valid Usuario usuario, BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		try {
			Map<String, Object> errors = usuarioService.validarTodo(usuario, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean usuarioNew = usuarioService.saveAdmin(usuario);
			if (!usuarioNew) {
				salida.put("mensaje", "No se pudo registar al usuario!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Se registro el usuario existosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar al usuario!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/v1/usuarios")
	public ResponseEntity<?> actualizar(@RequestBody @Valid Usuario usuario, BindingResult result,
			HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		Optional<Usuario> usuOp = usuarioService.findByCodigo(usuario.getCodigo());
		if (usuOp.isEmpty()) {
			salida.put("mensaje", "El usuario no existe!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		try {
			Map<String, Object> errors = usuarioService.validarTodo(usuario, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean usuarioNew = usuarioService.updateAdmin(usuario);
			if (!usuarioNew) {
				salida.put("mensaje", "No se pudo actualizar el usuario!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "El usuario fue actualizado con exito!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/v1/usuarios/{codigo}")
	public ResponseEntity<?> eliminar(@PathVariable String codigo) {
		Map<String, Object> salida = new HashMap<>();
		Optional<Usuario> existeUsu = usuarioService.findByCodigo(codigo);
		if (existeUsu.isEmpty()) {
			salida.put("mensaje", "El usuario no existe!");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		try {
			boolean usuarioDel = usuarioService.delete(existeUsu.get().getId());
			if (!usuarioDel) {
				salida.put("mensaje", "No se pudo eliminar al usuario!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "El usuario se elimino exitosamente");
			return ResponseEntity.ok(salida);
		} catch (Exception e) {
			salida.put("mensaje", "Hubo un error al eliminar al usuario: " + e.getMessage());
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/roles")
	public ResponseEntity<List<RolDto>> listarRoles() {
		List<RolDto> lista = rolService.listarRoles();
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/carteras")
	public ResponseEntity<?> buscar(HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		Optional<CarteraDTO> cartera = carteraService.findByUsuarioIdDto(usuSession.getId());
		if (cartera.isEmpty()) {
			salida.put("mensaje", "El no se encontro la cartera, contacta con un administrador!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(cartera);
	}
}
