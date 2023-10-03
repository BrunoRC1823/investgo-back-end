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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.investgo.app.dto.TransaccionDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.entity.TipoTransaccion;
import com.proyecto.investgo.app.entity.Transaccion;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.service.CarteraService;
import com.proyecto.investgo.app.service.CuentaBancariaService;
import com.proyecto.investgo.app.service.TipoTransaccionService;
import com.proyecto.investgo.app.service.TransaccionService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class TransaccionController {
	@Autowired
	private TransaccionService transaccionService;
	@Autowired
	private TipoTransaccionService tipoTransaccionService;
	@Autowired
	private CuentaBancariaService cuentaBancariaService;
	@Autowired
	private CarteraService carteraService;
	@Autowired
	private IValidationEntityService valService;

	@GetMapping("/v1/transacciones/listar-tipo/{idTipo}")
	public ResponseEntity<?> listarxUsuarioActual(@PathVariable long idTipo, Paginador paginador, HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		Optional<TipoTransaccion> tipo = tipoTransaccionService.findById(idTipo);
		if (tipo.isEmpty()) {
			salida.put("mensaje", "No se encontro el tipo de transaccion enviado!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		Page<TransaccionDTO> lista = transaccionService
				.findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigoDto(idTipo, usuSession.getId(),
						paginador);
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/transacciones/listar-cuenta/{codigoCuenta}")
	public ResponseEntity<?> listarxIdCuentaBancaria(@PathVariable String codigoCuenta, Paginador paginador,
			HttpSession session) {
		HashMap<String, Object> salida = new HashMap<>();
		Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
		boolean existe = cuentaBancariaService.validarPropietarioCuenta(usuSession.getId(), codigoCuenta);
		if (!existe) {
			salida.put("mensaje", "La cuenta bancaria no existe!");
			return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
		}
		Optional<CuentaBancaria> ctaOp = cuentaBancariaService.findByCodigo(codigoCuenta);
		Page<TransaccionDTO> lista = transaccionService.findAllByCuentaBancariaIdOrderByCodigoDto(ctaOp.get().getId(),
				paginador);
		return ResponseEntity.ok(lista);

	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PostMapping("/v1/transacciones/deposito")
	public ResponseEntity<?> depositarCuenta(@RequestBody @Valid Transaccion transaccion, BindingResult result,
			HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		if (result.hasErrors()) {
			salida = valService.validar(result);
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
			boolean existe = cuentaBancariaService.validarPropietarioCuenta(usuSession.getId(),
					transaccion.getCuentaBancaria().getCodigo());
			if (!existe) {
				salida.put("mensaje", "La cuenta bancaria no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			Optional<Cartera> carteraOp = carteraService.findByUsuarioId(usuSession.getId());
			Optional<CuentaBancaria> ctaOp = cuentaBancariaService
					.findByCodigo(transaccion.getCuentaBancaria().getCodigo());
			if (transaccion.getMonto().compareTo(ctaOp.get().getSaldo()) > 0) {
				salida.put("mensaje", "No cuenta con saldo suficiente en su cuenta Bancaria!");
				return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
			}
			boolean newTrans = transaccionService.saveDeposito(transaccion, ctaOp.get(), carteraOp.get());
			if (!newTrans) {
				salida.put("mensaje", "No se registro el deposito!");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Deposito realizado con exito");
			return ResponseEntity.ok(salida);

		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar el deposito");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PostMapping("/v1/transacciones/retiro")
	public ResponseEntity<?> retirarCuenta(@RequestBody @Valid Transaccion transaccion, BindingResult result,
			HttpSession session) {
		Map<String, Object> salida = new HashMap<>();
		if (result.hasErrors()) {
			salida = valService.validar(result);
			return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
		}
		try {
			Usuario usuSession = (Usuario) session.getAttribute("usuarioSession");
			boolean existe = cuentaBancariaService.validarPropietarioCuenta(usuSession.getId(),
					transaccion.getCuentaBancaria().getCodigo());
			if (!existe) {
				salida.put("mensaje", "La cuenta bancaria no existe!");
				return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
			}
			Optional<Cartera> carteraOp = carteraService.findByUsuarioId(usuSession.getId());
			Optional<CuentaBancaria> ctaOp = cuentaBancariaService
					.findByCodigo(transaccion.getCuentaBancaria().getCodigo());
			if (transaccion.getMonto().compareTo(carteraOp.get().getSaldo()) > 0) {
				salida.put("mensaje", "No cuenta con saldo suficiente en su cartera!");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			boolean newTrans = transaccionService.saveRetiro(transaccion, ctaOp.get(), carteraOp.get());
			if (!newTrans) {
				salida.put("mensaje", "No se registro la transaccion!");
				return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
			}
			salida.put("mensaje", "Retiro realizado con exito");
			return ResponseEntity.ok(salida);
		} catch (DataAccessException e) {
			salida.put("mensaje", "Error al registrar el retiro");
			salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@GetMapping("/v1/tipos-transacciones")
	public ResponseEntity<List<TipoTransaccion>> listarTiposTransacciones() {
		List<TipoTransaccion> lista = tipoTransaccionService.findAll();
		return ResponseEntity.ok(lista);
	}
}
