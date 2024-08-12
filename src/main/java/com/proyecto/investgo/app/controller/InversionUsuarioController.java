package com.proyecto.investgo.app.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.proyecto.investgo.app.utils.service.IDecoderTokenService;
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

import com.proyecto.investgo.app.dto.InversionUsuarioDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.entity.InversionUsuario;
import com.proyecto.investgo.app.entity.OportunidadInversion;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.service.CarteraService;
import com.proyecto.investgo.app.service.InversionUsuarioService;
import com.proyecto.investgo.app.service.OportunidadInversionService;
import com.proyecto.investgo.app.service.UsuarioService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@RestController
@RequestMapping(DefaultValues.URL_API)
@CrossOrigin(origins = DefaultValues.URL_CROSS_ORIGIN)
public class InversionUsuarioController {

    @Autowired
    private InversionUsuarioService inversionUsuarioservice;
    @Autowired
    private OportunidadInversionService opoInvService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CarteraService carteraService;
    @Autowired
    private IValidationEntityService valService;
    @Autowired
    private IDecoderTokenService decoderService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/v1/inversiones/buscar-invesion/{codigo}")
    public ResponseEntity<?> buscar(@PathVariable String codigo, HttpServletRequest request) {
        Map<String, Object> salida = new HashMap<>();
        String idU = decoderService.getIdUser(request);
        Optional<InversionUsuarioDTO> opoInvOp = inversionUsuarioservice.findByCodigoAndUsuarioIdDto(codigo,
                idU);
        if (opoInvOp.isEmpty()) {
            salida.put("mensaje", "No existe la inversion!");
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(opoInvOp.get());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/v1/inversiones/listar-oportunidad/{codigoOpo}")
    public ResponseEntity<?> listarXOportunidad(@PathVariable String codigoOpo, Paginador paginador) {
        Map<String, Object> salida = new HashMap<>();
        Optional<OportunidadInversion> opoInvOp = opoInvService.findByCodigo(codigoOpo);
        if (opoInvOp.isEmpty()) {
            salida.put("mensaje", "No existe la oportunidad de inverision!");
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
        Page<InversionUsuarioDTO> lista = inversionUsuarioservice
                .findByOportunidadInversionIdDto(opoInvOp.get().getId(), paginador);
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/v1/inversiones/listar-inversiones/{codigoOpo}")
    public ResponseEntity<?> buscarInvercionesXOportunidadUsuario(@PathVariable String codigoOpo, Paginador paginador,
                                                                  HttpServletRequest request) {
        Map<String, Object> salida = new HashMap<>();
        Optional<OportunidadInversion> opoInvOp = opoInvService.findByCodigo(codigoOpo);
        if (opoInvOp.isEmpty()) {
            salida.put("mensaje", "No existe la oportunidad de inverision!");
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
        String idU = decoderService.getIdUser(request);
        Page<InversionUsuarioDTO> lista = inversionUsuarioservice
                .findByOportunidadInversionIdAndUsuarioIdDto(opoInvOp.get().getId(), idU, paginador);
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/v1/inversiones/listar-enable/{enable}")
    public ResponseEntity<?> buscarInvercionesXUsuarioPagadas(
            @PathVariable(value = DefaultValues.DEFAULT_ENABLE) String enable, Paginador paginador,
            HttpServletRequest request) {
        String idU = decoderService.getIdUser(request);
        boolean valorEnable = enable.equals(DefaultValues.DEFAULT_ENABLE);
        Page<InversionUsuarioDTO> lista = inversionUsuarioservice
                .findByUsuarioIdAndAuditoriaEnableDto(idU, valorEnable, paginador);
        return ResponseEntity.ok(lista);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/v1/inversiones")
    public ResponseEntity<?> guardar(@RequestBody @Valid InversionUsuario objOpUsu, BindingResult result,
                                     HttpServletRequest request) {
        Map<String, Object> salida = new HashMap<>();
        if (result.hasErrors()) {
            salida = valService.validar(result);
            return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
        }
        String idU = decoderService.getIdUser(request);
        try {
            Optional<Cartera> cartera = carteraService.findByUsuarioId(idU);
            BigDecimal saldoCartera = cartera.get().getSaldo();
            BigDecimal montoInvertido = objOpUsu.getMontoInvertido();
            boolean esMayorMontoInver = montoInvertido.compareTo(saldoCartera) > 0;
            if (esMayorMontoInver) {
                salida.put("mensaje", "No cuenta con saldo suficiente en su cartera");
                return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
            }
            Optional<OportunidadInversion> existeOpInver = opoInvService
                    .findByCodigo(objOpUsu.getOportunidadInversion().getCodigo());
            if (existeOpInver.isEmpty()) {
                salida.put("mensaje", "No se encontro la oportunidad de inversion");
                return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
            }
            if (!existeOpInver.get().getAuditoria().getEnable()) {
                salida.put("mensaje", "La subasta fue cerrada. Lo sentimos");
                return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
            }
            BigDecimal montoRecaudadoActual = existeOpInver.get().getMontoRecaudado();
            BigDecimal montoRecaudadoActualizado = montoRecaudadoActual.add(objOpUsu.getMontoInvertido());
            BigDecimal montoOpInversion = existeOpInver.get().getMonto();
            BigDecimal restante = montoOpInversion.subtract(montoRecaudadoActual);
            boolean esMayorMontoRecaudado = montoRecaudadoActualizado.compareTo(montoOpInversion) > 0;
            if (esMayorMontoRecaudado) {
                salida.put("mensaje", "La Inversion Excede El Monto Solicitado. "
                        + "Ingrese una inversion menor o igual a: " + restante);
                return new ResponseEntity<>(salida, HttpStatus.CONFLICT);
            }
            boolean completado = montoRecaudadoActualizado.compareTo(montoOpInversion) == 0;
            if (completado) {
                salida.put("completado", "Felicidades!, con su inversion se alcanzo la meta.");
            }
            Optional<Usuario> usuOp = usuarioService.findById(idU);
            objOpUsu.setUsuario(usuOp.get());
            objOpUsu.setOportunidadInversion(existeOpInver.get());
            boolean newInversion = inversionUsuarioservice.saveInversion(objOpUsu);
            if (!newInversion) {
                salida.put("mensaje", "No se registro la inversion");
                return new ResponseEntity<>(salida, HttpStatus.BAD_REQUEST);
            }
            salida.put("mensaje", "La inversion se realizo con exito");
            return ResponseEntity.ok(salida);
        } catch (DataAccessException e) {
            salida.put("mensaje", "Error al registrar la Inversion");
            salida.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(salida, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
