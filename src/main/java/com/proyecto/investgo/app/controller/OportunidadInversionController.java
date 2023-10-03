package com.proyecto.investgo.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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

import com.proyecto.investgo.app.dto.FacturaDTO;
import com.proyecto.investgo.app.dto.OportunidadInversionDTO;
import com.proyecto.investgo.app.entity.Empresa;
import com.proyecto.investgo.app.entity.Factura;
import com.proyecto.investgo.app.entity.OportunidadInversion;
import com.proyecto.investgo.app.service.EmpresaService;
import com.proyecto.investgo.app.service.FacturaService;
import com.proyecto.investgo.app.service.OportunidadInversionService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class OportunidadInversionController {

	@Autowired
	private OportunidadInversionService oportunidadInversionService;
	@Autowired
	private FacturaService facturaService;
	@Autowired
	private EmpresaService empresaService;
	@Autowired
	private IValidationEntityService valService;

	private List<Factura> facturaList = new ArrayList<>();

	private ModelMapper modelMapper = new ModelMapper();

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/v1/add-factura")
	public ResponseEntity<?> addFacturaList(@RequestBody Map<String, Object> request) {
		HashMap<String, Object> salida = new HashMap<>();
		String codigoFac = (String) request.get("codigoFactura");
		String codigoEmp = (String) request.get("codigoEmpresa");
		Optional<Factura> facturaOp = facturaService.findByCodigo(codigoFac);
		if (facturaOp.isEmpty()) {
			salida.put("mensaje", "No se encontro la factura que desea agregar!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		Optional<Empresa> empresaOptional = empresaService.findByCodigo(codigoEmp);
		if (empresaOptional.isEmpty()) {
			salida.put("mensaje", "No se encontró la empresa");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		List<Factura> listaFacturaXEmpre = facturaService
				.findAllByEmpresaIdAndAuditoriaEnable(empresaOptional.get().getId(), true);
		boolean facPertenece = listaFacturaXEmpre.stream().anyMatch(f -> f.getCodigo().equals(codigoFac));
		if (!facPertenece) {
			salida.put("mensaje", "Ocurrio un error al añadir la factura!");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		Boolean ingresado = facturaList.stream().anyMatch(f -> f.getCodigo().equals(codigoFac));
		if (ingresado) {
			salida.put("mensaje", "Ups, parece que ya Ingreso esa factura");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		facturaList.add(facturaOp.get());
		salida.put("mensaje", "Se agrego correctamente la factura: " + codigoFac);
		return ResponseEntity.ok(salida);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/v1/delete-factura/{codigoFac}")
	public ResponseEntity<?> eliminarFacturaList(@PathVariable String codigoFac) {
		HashMap<String, Object> salida = new HashMap<>();
		if (facturaList.isEmpty()) {
			salida.put("mensaje", "La lista está vacía!");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		Optional<Factura> facturaAEliminar = facturaList.stream().filter(f -> f.getCodigo().equals(codigoFac))
				.findFirst();
		if (facturaAEliminar.isEmpty()) {
			salida.put("mensaje", "La lista de facturas no contiene el código enviado");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		facturaList.remove(facturaAEliminar.get());
		salida.put("mensaje", "Se retiro de la lista con exito la factura: " + facturaAEliminar.get().getCodigo());
		return ResponseEntity.ok(salida);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/v1/clear-lista")
	public void limpiarFacturaList() {
		facturaList.clear();
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/oportunidades-inversion/buscar-activo/{codigo}")
	public ResponseEntity<?> buscarPorCodigoActivo(@PathVariable String codigo) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<OportunidadInversionDTO> oportunidad = oportunidadInversionService
				.findByCodigoAndAuditoriaEnableDto(codigo, true);
		if (oportunidad.isEmpty()) {
			salida.put("mensaje", "No se encontro la Oportunidad de inversion!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(oportunidad);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/oportunidades-inversion/listar-activos")
	public ResponseEntity<Page<OportunidadInversionDTO>> listarActive(Paginador paginador) {
		Page<OportunidadInversionDTO> lista = oportunidadInversionService.findAllByAuditoriaEnableDto(true, paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/oportunidades-inversion/listar-enable/{enable}")
	public ResponseEntity<Page<OportunidadInversionDTO>> listarEnable(
			@PathVariable(value = DefaultValues.DEFAULT_ENABLE) String enable, Paginador paginador) {
		boolean valorEnable = enable.equals(DefaultValues.DEFAULT_ENABLE);
		Page<OportunidadInversionDTO> lista = oportunidadInversionService.findAllByAuditoriaEnableDto(valorEnable,
				paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/oportunidades-inversion/buscar/{codigo}")
	public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<OportunidadInversionDTO> oportunidad = oportunidadInversionService.findByCodigoDto(codigo);
		if (oportunidad.isEmpty()) {
			salida.put("mensaje", "No se encontro la Oportunidad de inversion!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		List<Factura> lista = facturaService.findAllFacturasByOportunidadInversionCodigo(codigo);
		facturaList = lista;
		List<FacturaDTO> listaMap = lista.stream().map(f -> modelMapper.map(f, FacturaDTO.class)).toList();
		salida.put("oportunidad", oportunidad.get());
		salida.put("facturas", listaMap);
		return new ResponseEntity<>(salida, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/v1/oportunidades-inversion/buscar-facturas/{codOportunidad}")
	public ResponseEntity<?> listadoFacturasXIdOpo(@PathVariable String codOportunidad) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<OportunidadInversionDTO> oportunidadOp = oportunidadInversionService.findByCodigoDto(codOportunidad);
		if (oportunidadOp.isEmpty()) {
			salida.put("mensaje", "No se encontro la oportunidad de inversion!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		List<Factura> lista = facturaService.findAllFacturasByOportunidadInversionCodigo(codOportunidad);
		List<FacturaDTO> listaMap = lista.stream().map(f -> modelMapper.map(f, FacturaDTO.class)).toList();
		salida.put("oportunidad", oportunidadOp.get());
		salida.put("facturas", listaMap);
		return new ResponseEntity<>(salida, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/v1/oportunidades-inversion")
	public ResponseEntity<?> guardar(@RequestBody @Valid OportunidadInversion opoInv, BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		if (facturaList.isEmpty()) {
			salida.put("mensaje", "Debe agregar facturas para poder registrar la Oportunidad de Inversion!");
			return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
		}
		try {
			if (result.hasErrors()) {
				salida = valService.validar(result);
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			Optional<Empresa> empresaOptional = empresaService.findByCodigo(opoInv.getEmpresa().getCodigo());
			if (empresaOptional.isEmpty()) {
				salida.put("mensaje", "No se encontró la empresa");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			boolean facPertenece = facturaList.stream()
					.anyMatch(f -> f.getEmpresa().getCodigo().equals(opoInv.getEmpresa().getCodigo()));
			if (!facPertenece) {
				salida.put("mensaje", "Ocurrio un error al añadir la factura!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			facturaList.forEach(opoInv::addFactura);
			opoInv.setEmpresa(empresaOptional.get());
			boolean newOpo = oportunidadInversionService.saveOportunidad(opoInv);
			facturaList.clear();
			if (!newOpo) {
				salida.put("mensaje", "No se registro la oportunidad de inversion!");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "La oportunidad de inversion se registro exitosamente!");
			return ResponseEntity.ok(salida);

		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar el Oportunidad Inversion");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/v1/oportunidades-inversion")
	public ResponseEntity<?> actualizar(@RequestBody @Valid OportunidadInversion opoInv, BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		if (result.hasErrors()) {
			salida = valService.validar(result);
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Optional<OportunidadInversion> opoInvOp = oportunidadInversionService.findByCodigo(opoInv.getCodigo());
			if (opoInvOp.isEmpty()) {
				salida.put("mensaje", "No existe oportunidad inversion");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			if (opoInvOp.get().getEnProceso()) {
				salida.put("mensaje", "La oportunidad de inversion ya inicio "
						+ "su proceso de recaudacion. No puede ser actualizada");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			if (facturaList.isEmpty()) {
				salida.put("mensaje", "Debe agregar facturas para poder actualizar la Oportunidad de Inversion!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			boolean facPertenece = facturaList.stream()
					.anyMatch(f -> f.getEmpresa().getCodigo().equals(opoInv.getEmpresa().getCodigo()));
			if (!facPertenece) {
				salida.put("mensaje", "Ocurrio un error al añadir la factura!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			facturaList.forEach(opoInv::addFactura);
			opoInv.setEmpresa(opoInvOp.get().getEmpresa());
			boolean newOpo = oportunidadInversionService.updateOportunidad(opoInv);
			facturaList.clear();
			if (!newOpo) {
				salida.put("mensaje", "No se actualizo la oportunidad de inversion");
				return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			salida.put("mensaje", "La oportunidad de inversion se actualizo exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al actualizar el Oportunidad Inversion");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/v1/oportunidades-inversion/{codigo}")
	public ResponseEntity<?> eliminarOportunidad(@PathVariable String codigo) {
		HashMap<String, Object> salida = new HashMap<>();
		try {
			Optional<OportunidadInversion> existeOportunidad = oportunidadInversionService.findByCodigo(codigo);
			if (existeOportunidad.isEmpty()) {
				salida.put("mensaje", "No existe oportunidad de inversion");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			if (existeOportunidad.get().getEnProceso()) {
				salida.put("mensaje",
						"La oportunidad de inversion ya inicio su proceso de recaudacion. No puede ser eliminada");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			boolean oportunidaDel = oportunidadInversionService.delete(existeOportunidad.get().getId());
			if (!oportunidaDel) {
				salida.put("mensaje", "No se elimino la oportunidad de inversion");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "La oportunidad de inversion se elimino exitosamente!");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al eliminar el Oportunidad Inversion");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
