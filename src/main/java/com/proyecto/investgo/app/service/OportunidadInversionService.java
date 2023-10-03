package com.proyecto.investgo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.proyecto.investgo.app.dto.OportunidadInversionDTO;
import com.proyecto.investgo.app.entity.OportunidadInversion;
import com.proyecto.investgo.app.utils.Paginador;

public interface OportunidadInversionService {
	Boolean save(OportunidadInversion oportunidad);

	Boolean saveOportunidad(OportunidadInversion oportunidad);

	Boolean updateOportunidad(OportunidadInversion oportunidad);

	Boolean delete(String id);

	Optional<OportunidadInversion> findByCodigo(String codigo);

	Optional<OportunidadInversionDTO> findByCodigoDto(String codigo);

	Optional<OportunidadInversionDTO> findByCodigoAndAuditoriaEnableDto(String codigo, boolean enable);

	List<OportunidadInversion> findAllByAuditoriaEnableTrueWithFacturas();

	List<OportunidadInversion> findAllByPagadoFalseWithFacturas();

	Page<OportunidadInversionDTO> findAllByAuditoriaEnableDto(boolean enable, Paginador paginador);

	Page<OportunidadInversionDTO> findAllByEmpresaCodigoDto(String idCodigo, Paginador paginador);

	OportunidadInversion asignarValores(OportunidadInversion oportunidad);

}
