package com.proyecto.investgo.app.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.dto.InversionUsuarioDTO;
import com.proyecto.investgo.app.entity.InversionUsuario;
import com.proyecto.investgo.app.repository.InversionUsuarioRepository;
import com.proyecto.investgo.app.service.InversionUsuarioService;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;

@Service
public class InversionUsuarioServiceImpl implements InversionUsuarioService {

	@Autowired
	InversionUsuarioRepository inversionRepo;

	@Autowired
	private IGeneratorCodigoService codGenService;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(InversionUsuario invUsu) {
		InversionUsuario invUsuNew = inversionRepo.save(invUsu);
		if (invUsuNew == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean saveInversion(InversionUsuario invUsu) {
		if (invUsu.getId() != null || "".equals(invUsu.getId())) {
			return false;
		}
		if (invUsu.getCodigo() != null || "".equals(invUsu.getCodigo())) {
			return false;
		}
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_INVERSION.getPrefix(),
				(int) inversionRepo.count());
		modelMapper.map(identificador, invUsu);
		invUsu.setGanancia(invUsu.calcularGanancia());
		BigDecimal montoInvertido = invUsu.getMontoInvertido();
		BigDecimal nuevoSaldo = invUsu.getUsuario().getCartera().getSaldo().subtract(montoInvertido);
		invUsu.getUsuario().getCartera().setSaldo(nuevoSaldo);
		BigDecimal nuevoMontoRecaudado = invUsu.getOportunidadInversion().getMontoRecaudado().add(montoInvertido);
		invUsu.getOportunidadInversion().setMontoRecaudado(nuevoMontoRecaudado);
		BigDecimal montoOpInversion = invUsu.getOportunidadInversion().getMonto();
		boolean completado = nuevoMontoRecaudado.compareTo(montoOpInversion) == 0;
		if (!invUsu.getOportunidadInversion().getEnProceso()) {
			invUsu.getOportunidadInversion().setEnProceso(true);
		}
		if (completado) {
			invUsu.getOportunidadInversion().getAuditoria().setEnable(false);
			invUsu.getOportunidadInversion().setTerminado(true);
		}
		return save(invUsu);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<InversionUsuarioDTO> findByCodigoAndUsuarioIdDto(String codigo, String idUsu) {
		Optional<InversionUsuario> inversion = inversionRepo.findByCodigoAndUsuarioId(codigo, idUsu);
		if (inversion.isEmpty()) {
			return Optional.empty();
		}
		InversionUsuarioDTO inversionDto = modelMapper.map(inversion.get(), InversionUsuarioDTO.class);
		return Optional.ofNullable(inversionDto);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InversionUsuario> findAllByOportunidadInversionIdWithOportunidadInversionAndUsuario(
			String oportunidadInversionId) {
		return inversionRepo.findAllByOportunidadInversionIdWithOportunidadInversionAndUsuario(oportunidadInversionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InversionUsuarioDTO> findByOportunidadInversionIdDto(String idOpo, Paginador paginador) {
		Page<InversionUsuario> lista = inversionRepo.findByOportunidadInversionId(idOpo, paginador.getPageable());
		return lista.map(iU -> modelMapper.map(iU, InversionUsuarioDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InversionUsuarioDTO> findByOportunidadInversionIdAndUsuarioIdDto(String idOpo, String idUsu,
			Paginador paginador) {
		Page<InversionUsuario> lista = inversionRepo.findByOportunidadInversionIdAndUsuarioId(idOpo, idUsu,
				paginador.getPageable());
		return lista.map(iU -> modelMapper.map(iU, InversionUsuarioDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InversionUsuarioDTO> findByUsuarioIdAndAuditoriaEnableDto(String idUsu, boolean enable,
			Paginador paginador) {
		Page<InversionUsuario> lista = inversionRepo.findByUsuarioIdAndAuditoriaEnable(idUsu, enable,
				paginador.getPageable());
		return lista.map(iU -> modelMapper.map(iU, InversionUsuarioDTO.class));
	}

}
