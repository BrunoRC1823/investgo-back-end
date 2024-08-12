package com.proyecto.investgo.app.repository;

import java.util.List;
import java.util.Optional;

import com.proyecto.investgo.app.dto.TransaccionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, String> {
	Optional<Transaccion> findByCodigo(String codigo);

	List<Transaccion> findAllByCuentaBancariaIdOrderByCodigo(String idCuenta);

	List<Transaccion> findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigo(long idTipo, String idUsu);

	Page<Transaccion> findAllByCuentaBancariaIdOrderByCodigo(String idCuenta, Pageable pageable);

	Page<Transaccion> findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigo(long idTipo, String idUsu,
			Pageable pageable);
	Page<Transaccion> findAllByCuentaBancariaUsuarioId(String idUsu, Pageable pageable);
}
