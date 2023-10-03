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

import com.proyecto.investgo.app.dto.EmpresaDTO;
import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.entity.Empresa;
import com.proyecto.investgo.app.repository.EmpresaRepository;
import com.proyecto.investgo.app.service.EmpresaService;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;
import com.proyecto.investgo.app.utils.service.IValidationEntityService;

@Service
public class EmpresaServiceImpl implements EmpresaService {

	@Autowired
	EmpresaRepository empRepo;

	@Autowired
	private IGeneratorCodigoService codGenService;

	@Autowired
	private IValidationEntityService valService;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(Empresa emp) {
		Empresa empNew = empRepo.save(emp);
		if (empNew == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		Optional<Empresa> empAct = empRepo.findById(id);
		if (empAct.isEmpty()) {
			return false;
		}
		empRepo.delete(empAct.get());
		return true;
	}

	@Override
	@Transactional
	public Boolean saveEmpresa(Empresa empresa) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_EMPRESA.getPrefix(),
				(int) empRepo.count());
		modelMapper.map(identificador, empresa);
		return save(empresa);
	}

	@Override
	@Transactional
	public Boolean updateEmpresa(Empresa empresa) {
		Optional<Empresa> empO = empRepo.findByCodigo(empresa.getCodigo());
		if (empO.isEmpty()) {
			return false;
		}
		empresa.setId(empO.get().getId());
		empresa.setCodigo(empO.get().getCodigo());
		empresa.setAuditoria((empO.get().getAuditoria()));
		empresa.setRiesgo(empO.get().getRiesgo());
		return save(empresa);
	}

	@Override
	@Transactional
	public Boolean deshabilitarEmpresa(Empresa empresa) {
		empresa.getAuditoria().setEnable(false);
		return save(empresa);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Empresa> findById(String id) {
		return empRepo.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Empresa> findByCodigo(String codigo) {
		return empRepo.findByCodigo(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<EmpresaDTO> findByCodigoDto(String codigo) {
		Optional<Empresa> empOp = empRepo.findByCodigo(codigo);
		if (empOp.isEmpty()) {
			return Optional.empty();
		}
		EmpresaDTO empDto = modelMapper.map(empOp.get(), EmpresaDTO.class);
		return Optional.ofNullable(empDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EmpresaDTO> findAllByAuditoriaEnableDto(boolean enable, Paginador paginador) {
		Page<Empresa> empresas = empRepo.findAllByAuditoriaEnable(enable, paginador.getPageable());
		return empresas.map(e -> modelMapper.map(e, EmpresaDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EmpresaDTO> findAllByRazonSocialContainingAndAuditoriaEnableDto(String keyword, boolean enable,
			Paginador paginador) {
		Page<Empresa> empresas = empRepo.findAllByRazonSocialContainingAndAuditoriaEnable(keyword, enable,
				paginador.getPageable());
		return empresas.map(e -> modelMapper.map(e, EmpresaDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> validarDatosEmpresa(Empresa empresa, String id) {
		Map<String, Object> salida = new HashMap<>();
		List<String> existen = new ArrayList<>();
		if (empRepo.existsByNombreAndIdNot(empresa.getNombre(), id)) {
			existen.add("La empresa: " + empresa.getNombre() + " ya existe!");
		}
		if (empRepo.existsByRucAndIdNot(empresa.getRuc(), id)) {
			existen.add("El Ruc: " + empresa.getRuc() + " ya existe!");
		}
		if (empRepo.existsByRazonSocialAndIdNot(empresa.getRazonSocial(), id)) {
			existen.add("La razon social : " + empresa.getRazonSocial() + " ya existe");
		}
		if (empRepo.existsByDireccionAndIdNot(empresa.getDireccion(), id)) {
			existen.add("La direccion : " + empresa.getDireccion() + " ya existe");
		}
		if (empRepo.existsByCorreoAndIdNot(empresa.getCorreo(), id)) {
			existen.add("El correo : " + empresa.getCorreo() + " ya existe");
		}
		if (empRepo.existsByNroCuentaBancariaAndIdNot(empresa.getNroCuentaBancaria(), id)) {
			existen.add("El numero de cuenta bancaria : " + empresa.getNroCuentaBancaria() + " ya existe");
		}
		if (existen.isEmpty()) {
			return salida;
		}
		salida.put("mensaje", existen);
		return salida;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<String, Object> validarTodo(Empresa empresa, BindingResult result) {
		Map<String, Object> errores = new HashMap<>();
		Optional<Empresa> empOp = findByCodigo(empresa.getCodigo());
		String valorId = empOp.isPresent() ? empOp.get().getId() : "";
		if (result.hasErrors()) {
			errores = valService.validar(result);
		} else {
			errores = validarDatosEmpresa(empresa, valorId);
		}
		return errores;
	}

}
