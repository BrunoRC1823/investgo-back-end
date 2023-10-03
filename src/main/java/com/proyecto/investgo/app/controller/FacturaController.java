package com.proyecto.investgo.app.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.proyecto.investgo.app.dto.FacturaDTO;
import com.proyecto.investgo.app.entity.Empresa;
import com.proyecto.investgo.app.entity.Factura;
import com.proyecto.investgo.app.service.EmpresaService;
import com.proyecto.investgo.app.service.FacturaService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class FacturaController {

	@Autowired
	private EmpresaService empresaService;
	@Autowired
	private FacturaService facturaService;
	@Autowired
	private IValidationEntityService valService;

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/v1/facturas/{codigo}")
	public ResponseEntity<?> buscarPorCod(@PathVariable String codigo) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<FacturaDTO> facturaOptional = facturaService.findByCodigoDto(codigo);
		if (facturaOptional.isEmpty()) {
			salida.put("mensaje", "No se encontró la factura");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(facturaOptional.get());
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/v1/facturas/buscar-fechas/{fechaInicio}&{fechaFin}")
	public ResponseEntity<?> listarPorFechas(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin, Paginador paginador) {
		HashMap<String, Object> salida = new HashMap<>();
		if (fechaInicio.isAfter(fechaFin)) {
			salida.put("mensaje", "La fecha de inicio tiene que ser anterior a la fecha de fin");
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Page<FacturaDTO> facturas = facturaService.findAllByFechaEmisionBetweenDto(fechaInicio, fechaFin,
					paginador);
			return ResponseEntity.ok(facturas);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al listar las facturas por rango de fechas");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/v1/facturas/buscar-activos/{codigoEmp}")
	public ResponseEntity<?> listarActivasPorEmpresa(@PathVariable String codigoEmp, Paginador paginador) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<Empresa> empresaOptional = empresaService.findByCodigo(codigoEmp);
		if (empresaOptional.isEmpty()) {
			salida.put("mensaje", "No se encontró la empresa");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		Page<FacturaDTO> facturas = facturaService
				.findAllByEmpresaIdAndAuditoriaEnableDto(empresaOptional.get().getId(), true, paginador);
		return ResponseEntity.ok(facturas);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/v1/facturas/buscar-enable/{codigoEmp}&{enable}")
	public ResponseEntity<?> listarPorEmpresaEnable(@PathVariable(name = "codigoEmp") String codigo,
			@PathVariable(name = DefaultValues.DEFAULT_ENABLE) String enable, Paginador paginador) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<Empresa> empresaOptional = empresaService.findByCodigo(codigo);
		if (empresaOptional.isEmpty()) {
			salida.put("mensaje", "No se encontró la empresa");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		boolean valorEnable = enable.equals(DefaultValues.DEFAULT_ENABLE);
		Page<FacturaDTO> facturas = facturaService
				.findAllByEmpresaIdAndAuditoriaEnableDto(empresaOptional.get().getId(), valorEnable, paginador);
		return ResponseEntity.ok(facturas);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping("/v1/facturas")
	public ResponseEntity<?> guardar(@Valid @RequestBody Factura factura, BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		if (result.hasErrors()) {
			salida = valService.validar(result);
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Optional<Empresa> empresaOptional = empresaService.findByCodigo(factura.getEmpresa().getCodigo());
			if (empresaOptional.isEmpty()) {
				salida.put("mensaje", "No se encontró la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			factura.setEmpresa(empresaOptional.get());
			boolean facNew = facturaService.saveFactura(factura);
			if (!facNew) {
				salida.put("mensaje", "Error al registrar la factura");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Factura registrada exitosamente");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar la factura");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PutMapping("/v1/facturas")
	public ResponseEntity<?> actualizar(@RequestBody @Valid Factura factura, BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		if (result.hasErrors()) {
			salida = valService.validar(result);
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Optional<Factura> facturaOptional = facturaService.findByCodigo(factura.getCodigo());
			if (facturaOptional.isEmpty()) {
				salida.put("mensaje", "No se encontró la factura!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			Optional<Empresa> empresaOptional = empresaService.findByCodigo(factura.getEmpresa().getCodigo());
			if (empresaOptional.isEmpty()) {
				salida.put("mensaje", "No se encontró la empresa!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			factura.setEmpresa(empresaOptional.get());
			boolean facNew = facturaService.updateFactura(factura);
			if (!facNew) {
				salida.put("mensaje", "No se actualizo la empresa");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Factura actualizada exitosamente");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar la factura");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping("/v1/facturas/{codigo}")
	public ResponseEntity<?> eliminar(@PathVariable String codigo) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Optional<Factura> facturaOptional = facturaService.findByCodigo(codigo);
			if (facturaOptional.isEmpty()) {
				salida.put("mensaje", "No se encontró la factura!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			facturaService.delete(facturaOptional.get().getId());
			salida.put("mensaje", "Se eliminó exitosamente la factura");
			return ResponseEntity.ok(salida);
		} catch (Exception e) {
			salida.put("mensaje", "Hubo un error al eliminar la factura: " + e.getMessage());
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
