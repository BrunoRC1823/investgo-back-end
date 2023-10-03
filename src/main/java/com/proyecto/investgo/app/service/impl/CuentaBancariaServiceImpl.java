package com.proyecto.investgo.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.proyecto.investgo.app.dto.CuentaBancariaDTO;
import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.repository.CuentaBancariaRepository;
import com.proyecto.investgo.app.service.CuentaBancariaService;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

	@Autowired
	private CuentaBancariaRepository cuentaRepo;
	@Autowired
	private IGeneratorCodigoService codGenService;
	@Autowired
	private IValidationEntityService valService;
	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(CuentaBancaria cta) {
		CuentaBancaria ctaNew = cuentaRepo.save(cta);
		if (ctaNew == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean saveCta(CuentaBancaria cta) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_CUENTA_BANCARIA.getPrefix(),
				(int) cuentaRepo.count());
		modelMapper.map(identificador, cta);
		return save(cta);
	}

	@Override
	@Transactional
	public Boolean updateCta(CuentaBancaria cta) {
		Optional<CuentaBancaria> ctaOp = findByCodigo(cta.getCodigo());
		if (ctaOp.isEmpty()) {
			return false;
		}
		cta.setId(ctaOp.get().getId());
		cta.setAuditoria((ctaOp.get().getAuditoria()));
		return save(cta);
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		Optional<CuentaBancaria> usuAct = findById(id);
		if (usuAct.isEmpty()) {
			return false;
		}
		cuentaRepo.delete(usuAct.get());
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CuentaBancaria> findById(String id) {
		return cuentaRepo.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CuentaBancaria> findByCodigo(String codigo) {
		return cuentaRepo.findByCodigo(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CuentaBancariaDTO> findByCodigoDto(String idUsu) {
		Optional<CuentaBancaria> cta = cuentaRepo.findByCodigo(idUsu);
		CuentaBancariaDTO ctaDto = modelMapper.map(cta, CuentaBancariaDTO.class);
		return Optional.ofNullable(ctaDto);
	}

	@Override
	public List<CuentaBancaria> findAllByUsuarioId(String idUsu) {
		return cuentaRepo.findAllByUsuarioId(idUsu);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CuentaBancariaDTO> findAllByUsuarioIdDto(String idUsu, Paginador paginador) {
		Page<CuentaBancaria> cuentas = cuentaRepo.findAllByUsuarioId(idUsu, paginador.getPageable());
		return cuentas.map(cta -> modelMapper.map(cta, CuentaBancariaDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> validarDatosCuenta(CuentaBancaria cta, String id) {
		Map<String, Object> salida = new HashMap<>();
		List<String> existen = new ArrayList<>();
		if (cuentaRepo.existsByNroCuentaAndIdNot(cta.getNroCuenta(), id)) {
			existen.add("El numero de cuenta bancaria: " + cta.getNroCuenta() + " ya existe!");
		}
		if (cuentaRepo.existsByNroCuentaCciAndIdNot(cta.getNroCuentaCci(), id)) {
			existen.add("El numero cci: " + cta.getNroCuentaCci() + " ya existe!");
		}
		if (existen.isEmpty()) {
			return salida;
		}
		salida.put("mensaje", existen);
		return salida;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> validarTodo(CuentaBancaria cta, BindingResult result) {
		Map<String, Object> errores = new HashMap<>();
		Optional<CuentaBancaria> ctaOp = findByCodigo(cta.getCodigo());
		String valorId = ctaOp.isPresent() ? ctaOp.get().getId() : "";
		if (result.hasErrors()) {
			errores = valService.validar(result);
		} else {
			errores = validarDatosCuenta(cta, valorId);
		}
		return errores;
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean validarPropietarioCuenta(String idUsu, String codigo) {
		List<CuentaBancaria> lista = cuentaRepo.findAllByUsuarioId(idUsu);
		return lista.stream().anyMatch(cuenta -> cuenta.getCodigo().equals(codigo));
	}

}
