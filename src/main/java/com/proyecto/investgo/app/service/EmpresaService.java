package com.proyecto.investgo.app.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import com.proyecto.investgo.app.dto.EmpresaDTO;
import com.proyecto.investgo.app.entity.Empresa;
import com.proyecto.investgo.app.utils.Paginador;

public interface EmpresaService {

	Boolean save(Empresa emp);

	Boolean delete(String id);

	Boolean saveEmpresa(Empresa empresa);

	Boolean updateEmpresa(Empresa empresa);

	Boolean deshabilitarEmpresa(Empresa empresa);

	Optional<Empresa> findById(String id);

	Optional<Empresa> findByCodigo(String codigo);

	Optional<EmpresaDTO> findByCodigoDto(String codigo);

	Page<EmpresaDTO> findAllByAuditoriaEnableDto(boolean enable, Paginador paginador);

	Page<EmpresaDTO> findAllByRazonSocialContainingAndAuditoriaEnableDto(String keyword, boolean enable,
			Paginador paginador);

	Map<String, Object> validarDatosEmpresa(Empresa empresa, String id);

	Map<String, Object> validarTodo(Empresa empresa, BindingResult result);

}
