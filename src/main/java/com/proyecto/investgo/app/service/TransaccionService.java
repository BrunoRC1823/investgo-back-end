package com.proyecto.investgo.app.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.proyecto.investgo.app.dto.TransaccionDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.entity.Transaccion;
import com.proyecto.investgo.app.utils.Paginador;
import org.springframework.data.domain.Pageable;

public interface TransaccionService {
	Boolean save(Transaccion trans);

	Boolean saveDeposito(Transaccion trans, CuentaBancaria cta, Cartera cartera);

	Boolean saveRetiro(Transaccion trans, CuentaBancaria cta, Cartera cartera);

	Optional<Transaccion> findByCodigo(String codigo);

	Optional<TransaccionDTO> findByCodigoDto(String codigo);

	Page<TransaccionDTO> findAllByCuentaBancariaIdOrderByCodigoDto(String idCuentaEmi, Paginador paginador);

	Page<TransaccionDTO> findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigoDto(long idTipo, String idUsu,
			Paginador paginador);
	Page<TransaccionDTO> findAllByCuentaBancariaUsuarioId(String idUsu, Paginador pageable);
}
