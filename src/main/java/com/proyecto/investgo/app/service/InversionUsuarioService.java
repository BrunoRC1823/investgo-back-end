package com.proyecto.investgo.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.proyecto.investgo.app.dto.InversionUsuarioDTO;
import com.proyecto.investgo.app.entity.InversionUsuario;
import com.proyecto.investgo.app.utils.Paginador;

public interface InversionUsuarioService {
	Boolean save(InversionUsuario invUsu);

	Boolean saveInversion(InversionUsuario invUsu);

	Optional<InversionUsuarioDTO> findByCodigoAndUsuarioIdDto(String codigo, String idUsu);

	List<InversionUsuario> findAllByOportunidadInversionIdWithOportunidadInversionAndUsuario(
			String oportunidadInversionId);

	Page<InversionUsuarioDTO> findByOportunidadInversionIdDto(String idOpo, Paginador paginador);

	Page<InversionUsuarioDTO> findByUsuarioIdAndAuditoriaEnableDto(String idUsu, boolean enable, Paginador paginador);

	Page<InversionUsuarioDTO> findByOportunidadInversionIdAndUsuarioIdDto(String idOpo, String idUsu,
			Paginador paginador);

}
