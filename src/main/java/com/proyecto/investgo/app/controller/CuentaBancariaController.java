package com.proyecto.investgo.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.proyecto.investgo.app.dto.CuentaBancariaDTO;
import com.proyecto.investgo.app.entity.Banco;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.entity.Moneda;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.service.BancosService;
import com.proyecto.investgo.app.service.CuentaBancariaService;
import com.proyecto.investgo.app.service.MonedasService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class CuentaBancariaController {

	@Autowired
	private CuentaBancariaService ctaService;
	@Autowired
	private BancosService bancoServices;
	@Autowired
	private MonedasService monedaService;

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/cuentas-bancarias/{codigo}")
	public ResponseEntity<?> buscar(@PathVariable String codigo, HttpSession session) {
		HashMap<String, Object> response = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		boolean esPropietario = ctaService.validarPropietarioCuenta(usuSession.getId(), codigo);
		if (!esPropietario) {
			response.put("mensaje", "La Cuenta Bancaria no existe");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(ctaService.findByCodigoDto(codigo));
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/cuentas-bancarias")
	public ResponseEntity<Page<CuentaBancariaDTO>> listarUsuAct(HttpSession session, Paginador paginador) {
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		Page<CuentaBancariaDTO> lista = ctaService.findAllByUsuarioIdDto(usuSession.getId(), paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PostMapping("/v1/cuentas-bancarias")
	public ResponseEntity<?> guardar(@RequestBody @Valid CuentaBancaria cta, BindingResult result,
			HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Map<String, Object> errors = ctaService.validarTodo(cta, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
			cta.addUsuario(usuSession.getId());
			Boolean ctaNew = ctaService.saveCta(cta);
			if (!ctaNew) {
				salida.put("mensaje", "No se pudo registrar la cuenta bancaria!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Su cuenta bancaria se registro exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar la cuenta bancaria");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PutMapping("/v1/cuentas-bancarias")
	public ResponseEntity<?> actualizar(@RequestBody @Valid CuentaBancaria cta, BindingResult result,
			HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		boolean esPropietario = ctaService.validarPropietarioCuenta(usuSession.getId(), cta.getCodigo());
		if (!esPropietario) {
			salida.put("mensaje", "La cuenta bancaria no existe!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		try {
			Map<String, Object> errors = ctaService.validarTodo(cta, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			cta.addUsuario(usuSession.getId());
			Boolean ctaNew = ctaService.updateCta(cta);
			if (!ctaNew) {
				salida.put("mensaje", "No se pudo actualizar la cuenta bancaria!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Se actualizo su cuenta bancaria exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar la cuenta bancaria");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@DeleteMapping("/v1/cuentas-bancarias/{codigo}")
	public ResponseEntity<?> eliminar(@PathVariable String codigo, HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
			boolean esPropietario = ctaService.validarPropietarioCuenta(usuSession.getId(), codigo);
			if (!esPropietario) {
				salida.put("mensaje", "La cuenta bancaria no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			Optional<CuentaBancaria> ctaOp = ctaService.findByCodigo(codigo);
			boolean objsalida = ctaService.delete(ctaOp.get().getId());
			if (!objsalida) {
				salida.put("mensaje", "No se pudo eliminar la cuenta bancaria");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Se elimino la cuenta bancaria!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Hubo un error al eliminar la cuenta bancaria");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/bancos")
	public ResponseEntity<List<Banco>> listarBanco() {
		List<Banco> lista = bancoServices.findAll();
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/monedas")
	public ResponseEntity<List<Moneda>> listarMoneda() {
		List<Moneda> lista = monedaService.findAll();
		return ResponseEntity.ok(lista);
	}
}
