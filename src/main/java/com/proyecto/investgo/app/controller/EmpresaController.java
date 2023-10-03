package com.proyecto.investgo.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import com.proyecto.investgo.app.dto.EmpresaDTO;
import com.proyecto.investgo.app.entity.Empresa;
import com.proyecto.investgo.app.service.EmpresaService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class EmpresaController {
	@Autowired
	private EmpresaService empresaService;

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/empresas/{codigo}")
	public ResponseEntity<?> buscar(@PathVariable String codigo) {
		Map<String, Object> salida = new HashMap<>();
		Optional<EmpresaDTO> empOp = empresaService.findByCodigoDto(codigo);
		if (empOp.isEmpty()) {
			salida.put("mensaje", "La empresa no existe!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(empOp.get());
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/listar-empresas/{enable}")
	public ResponseEntity<Page<EmpresaDTO>> listarEnable(
			@PathVariable(value = DefaultValues.DEFAULT_ENABLE) String enable, Paginador paginador) {
		boolean valorEnable = enable.equals(DefaultValues.DEFAULT_ENABLE);
		Page<EmpresaDTO> lista = empresaService.findAllByAuditoriaEnableDto(valorEnable, paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/v1/listar-empresas/buscar/{keyWord}")
	public ResponseEntity<Page<EmpresaDTO>> buscarActivasContains(@PathVariable String keyWord, Paginador paginador) {
		Page<EmpresaDTO> lista = empresaService.findAllByRazonSocialContainingAndAuditoriaEnableDto(keyWord, true,
				paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/v1/empresas")
	public ResponseEntity<?> guardar(@RequestBody @Valid Empresa empresa, BindingResult result) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Map<String, Object> errors = empresaService.validarTodo(empresa, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean empresaNew = empresaService.saveEmpresa(empresa);
			if (!empresaNew) {
				salida.put("mensaje", "No se pudo registrar la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Exito al registrar la empresa!");
			return ResponseEntity.ok(salida);

		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar empresa!");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("/v1/empresas")
	public ResponseEntity<?> actualizar(@RequestBody @Valid Empresa empresa, BindingResult result) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Optional<Empresa> empresaOp = empresaService.findByCodigo(empresa.getCodigo());
			if (empresaOp.isEmpty()) {
				salida.put("mensaje", "La empresa no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			Map<String, Object> errors = empresaService.validarTodo(empresa, result);
			if (!errors.isEmpty()) {
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			boolean empresaNew = empresaService.updateEmpresa(empresa);
			if (!empresaNew) {
				salida.put("mensaje", "No se pudo actualizar la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "Exito al actualizar la empresa!");
			return ResponseEntity.ok(salida);

		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar empresa");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("/v1/empresas/deshabilitar/{codigo}")
	public ResponseEntity<?> deshabilitar(@PathVariable String codigo) {
		Map<String, Object> salida = new HashMap<>();
		try {
			Optional<Empresa> empresaOp = empresaService.findByCodigo(codigo);
			if (empresaOp.isEmpty()) {
				salida.put("mensaje", "La empresa no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			if (!empresaOp.get().getAuditoria().getEnable()) {
				salida.put("mensaje", "La empresa ya esta deshabilitada!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			boolean empresaDel = empresaService.deshabilitarEmpresa(empresaOp.get());
			if (!empresaDel) {
				salida.put("mensaje", "No se pudo deshabilitar la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Se deshabilito la empresa con exito!");
			return ResponseEntity.ok(salida);
		} catch (Exception e) {
			salida.put("mensaje", "Hubo un error al deshabilitar la empresa: " + e.getMessage());
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/v1/empresas/{codigo}")
	public ResponseEntity<?> eliminar(@PathVariable String codigo) {
		Map<String, Object> salida = new HashMap<>();
		try {
			Optional<Empresa> empresaOp = empresaService.findByCodigo(codigo);
			if (empresaOp.isEmpty()) {
				salida.put("mensaje", "La empresa no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			boolean empresaDel = empresaService.delete(empresaOp.get().getId());
			if (!empresaDel) {
				salida.put("mensaje", "No se pudo eliminar la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Se elimino la empresa con exito!");
			return ResponseEntity.ok(salida);
		} catch (Exception e) {
			salida.put("mensaje", "Hubo un error al eliminar la empresa: " + e.getMessage());
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
