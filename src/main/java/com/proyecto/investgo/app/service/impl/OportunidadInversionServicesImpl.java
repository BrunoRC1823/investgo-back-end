package com.proyecto.investgo.app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.dto.OportunidadInversionDTO;
import com.proyecto.investgo.app.entity.Factura;
import com.proyecto.investgo.app.entity.OportunidadInversion;
import com.proyecto.investgo.app.repository.FacturaRepository;
import com.proyecto.investgo.app.repository.OportunidadInversionRepository;
import com.proyecto.investgo.app.service.OportunidadInversionService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;

@Service
public class OportunidadInversionServicesImpl implements OportunidadInversionService {

	@Autowired
	private OportunidadInversionRepository oportunidadRepo;
	@Autowired
	private FacturaRepository facturaRepo;
	@Autowired
	private IGeneratorCodigoService codGenService;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(OportunidadInversion oportunidad) {
		OportunidadInversion oportunidadNew = oportunidadRepo.save(oportunidad);
		if (oportunidadNew == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean saveOportunidad(OportunidadInversion oportunidad) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_OPORTUNIDAD_INVERSION.getPrefix(),
				(int) oportunidadRepo.count());
		modelMapper.map(identificador, oportunidad);
		return save(asignarValores(oportunidad));
	}

	@Override
	@Transactional
	public Boolean updateOportunidad(OportunidadInversion oportunidad) {
		Optional<OportunidadInversion> oportunidadOp = oportunidadRepo.findByCodigo(oportunidad.getCodigo());
		if (oportunidadOp.isEmpty()) {
			return false;
		}
		if (oportunidadOp.get().getEnProceso()) {
			return false;
		}
		oportunidad.setId(oportunidadOp.get().getId());
		oportunidad.setAuditoria((oportunidadOp.get().getAuditoria()));
		oportunidad.setMontoRecaudado(oportunidadOp.get().getMontoRecaudado());
		oportunidad.setEmpresa(oportunidadOp.get().getEmpresa());
		oportunidad.setEnProceso(oportunidadOp.get().getEnProceso());
		oportunidadOp.get().getFacturas().forEach(f -> {
			if (!oportunidad.getFacturas().contains(f)) {
				f.getAuditoria().setEnable(true);
				facturaRepo.save(f);
			}
		});
		return save(asignarValores(oportunidad));
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		Optional<OportunidadInversion> oportunidadAct = oportunidadRepo.findById(id);
		if (oportunidadAct.isEmpty()) {
			return false;
		}
		oportunidadAct.get().getFacturas().forEach(f -> {
			f.getAuditoria().setEnable(true);
			facturaRepo.save(f);
		});
		oportunidadRepo.delete(oportunidadAct.get());
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OportunidadInversion> findByCodigo(String codigo) {
		return oportunidadRepo.findByCodigo(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OportunidadInversionDTO> findByCodigoDto(String codigo) {
		Optional<OportunidadInversion> oportunidadOp = oportunidadRepo.findByCodigo(codigo);
		if (oportunidadOp.isEmpty()) {
			return Optional.empty();
		}
		OportunidadInversionDTO oportunidadDto = modelMapper.map(oportunidadOp.get(), OportunidadInversionDTO.class);
		return Optional.ofNullable(oportunidadDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OportunidadInversionDTO> findByCodigoAndAuditoriaEnableDto(String codigo, boolean enable) {
		Optional<OportunidadInversion> oportunidadOp = oportunidadRepo.findByCodigoAndAuditoriaEnable(codigo, enable);
		if (oportunidadOp.isEmpty()) {
			return Optional.empty();
		}
		OportunidadInversionDTO oportunidadDto = modelMapper.map(oportunidadOp.get(), OportunidadInversionDTO.class);
		return Optional.ofNullable(oportunidadDto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OportunidadInversion> findAllByAuditoriaEnableTrueWithFacturas() {
		return oportunidadRepo.findAllByAuditoriaEnableTrueWithFacturas();
	}

	@Override
	public List<OportunidadInversion> findAllByPagadoFalseWithFacturas() {
		return oportunidadRepo.findAllByPagadoFalseWithFacturas();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OportunidadInversionDTO> findAllByAuditoriaEnableDto(boolean enable, Paginador paginador) {
		Page<OportunidadInversion> lista = oportunidadRepo.findAllByAuditoriaEnable(enable, paginador.getPageable());
		return lista.map(oI -> modelMapper.map(oI, OportunidadInversionDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OportunidadInversionDTO> findAllByEmpresaCodigoDto(String idCodigo, Paginador paginador) {
		Page<OportunidadInversion> lista = oportunidadRepo.findAllByEmpresaCodigo(idCodigo, paginador.getPageable());
		return lista.map(oI -> modelMapper.map(oI, OportunidadInversionDTO.class));
	}

	@Override
	public OportunidadInversion asignarValores(OportunidadInversion oportunidad) {
		Optional<BigDecimal> montoTotal = Optional
				.of(oportunidad.getFacturas().stream().map(Factura::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add));
		if (montoTotal.isEmpty()) {
			return null;
		}
		Optional<LocalDate> fechaMaxima = oportunidad.getFacturas().stream().map(Factura::getFechaEmision)
				.max(LocalDate::compareTo);
		if (fechaMaxima.isEmpty()) {
			return null;
		}
		oportunidad.setMonto(montoTotal.get());
		oportunidad.setFechaCaducidad(fechaMaxima.get().plusMonths(DefaultValues.DEFAULT_MOUNTHS));
		oportunidad.setFechaPago(oportunidad.getFechaCaducidad().plusWeeks(DefaultValues.DEFAULT_WEEKS));
		oportunidad.getFacturas().forEach(f -> f.getAuditoria().setEnable(false));
		return oportunidad;
	}

}
