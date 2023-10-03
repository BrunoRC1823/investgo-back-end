package com.proyecto.investgo.app.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import com.proyecto.investgo.app.dto.CuentaBancariaDTO;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.utils.Paginador;

public interface CuentaBancariaService {

	Boolean save(CuentaBancaria cta);

	Boolean saveCta(CuentaBancaria cta);

	Boolean updateCta(CuentaBancaria cta);

	Boolean delete(String id);

	Optional<CuentaBancaria> findById(String id);

	Optional<CuentaBancaria> findByCodigo(String codigo);

	Optional<CuentaBancariaDTO> findByCodigoDto(String codigo);

	List<CuentaBancaria> findAllByUsuarioId(String idUsu);

	Page<CuentaBancariaDTO> findAllByUsuarioIdDto(String idUsu, Paginador paginador);

	Map<String, Object> validarDatosCuenta(CuentaBancaria cta, String id);

	Map<String, Object> validarTodo(CuentaBancaria cta, BindingResult result);

	Boolean validarPropietarioCuenta(String idUsu, String codigo);
}
