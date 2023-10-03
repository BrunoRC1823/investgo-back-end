package com.proyecto.investgo.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.investgo.app.dto.FacturaDTO;
import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.entity.Factura;
import com.proyecto.investgo.app.repository.FacturaRepository;
import com.proyecto.investgo.app.service.FacturaService;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;

@Service
public class FacturaServiceImpl implements FacturaService {

	@Autowired
	FacturaRepository facturaRepo;

	@Autowired
	private IGeneratorCodigoService codGenService;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(Factura fac) {
		Factura facNew = facturaRepo.save(fac);
		if (facNew == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean saveFactura(Factura fac) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_FACTURA.getPrefix(),
				(int) facturaRepo.count());
		modelMapper.map(identificador, fac);
		return save(fac);
	}

	@Override
	@Transactional
	public Boolean updateFactura(Factura fac) {
		Optional<Factura> facOp = facturaRepo.findByCodigo(fac.getCodigo());
		if (facOp.isEmpty()) {
			return false;
		}
		fac.setId(facOp.get().getId());
		fac.setAuditoria((facOp.get().getAuditoria()));
		return save(fac);
	}

	@Override
	@Transactional
	public Boolean delete(String id) {
		Optional<Factura> usuAct = facturaRepo.findById(id);
		if (usuAct.isEmpty()) {
			return false;
		}
		facturaRepo.delete(usuAct.get());
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Factura> findById(String id) {
		return facturaRepo.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Factura> findByCodigo(String cod) {
		return facturaRepo.findByCodigo(cod);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<FacturaDTO> findByCodigoDto(String cod) {
		Optional<Factura> facOp = facturaRepo.findByCodigo(cod);
		if (facOp.isEmpty()) {
			return Optional.empty();
		}
		FacturaDTO facDto = modelMapper.map(facOp.get(), FacturaDTO.class);
		return Optional.ofNullable(facDto);
	}

	@Override
	public List<Factura> findAllByEmpresaIdAndAuditoriaEnable(String idEmp, boolean enable) {

		return facturaRepo.findAllByEmpresaIdAndAuditoriaEnable(idEmp, enable);
	}

	@Override
	public List<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad) {
		return facturaRepo.findAllFacturasByOportunidadInversionCodigo(codOportunidad);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad, Paginador paginador) {
		return facturaRepo.findAllFacturasByOportunidadInversionCodigo(codOportunidad, paginador.getPageable());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FacturaDTO> findAllFacturasByOportunidadInversionCodigoDto(String codOportunidad, Paginador paginador) {
		Page<Factura> lista = facturaRepo.findAllFacturasByOportunidadInversionCodigo(codOportunidad,
				paginador.getPageable());
		return lista.map(f -> modelMapper.map(f, FacturaDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FacturaDTO> findAllByFechaEmisionBetweenDto(LocalDate fechaInicio, LocalDate fechaFin,
			Paginador paginador) {
		Page<Factura> lista = facturaRepo.findAllByFechaEmisionBetween(fechaInicio, fechaFin, paginador.getPageable());
		return lista.map(f -> modelMapper.map(f, FacturaDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FacturaDTO> findAllByEmpresaIdAndAuditoriaEnableDto(String idEmp, boolean enable, Paginador paginador) {
		Page<Factura> lista = facturaRepo.findAllByEmpresaIdAndAuditoriaEnable(idEmp, enable, paginador.getPageable());
		return lista.map(f -> modelMapper.map(f, FacturaDTO.class));
	}

}
