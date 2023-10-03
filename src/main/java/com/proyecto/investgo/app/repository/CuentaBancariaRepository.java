package com.proyecto.investgo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.CuentaBancaria;

public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, String> {
	List<CuentaBancaria> findAllByUsuarioId(String idUsu);

	Page<CuentaBancaria> findAllByUsuarioId(String idUsu, Pageable pageable);

	Optional<CuentaBancaria> findByCodigo(String codigo);

	Boolean existsByNroCuentaAndIdNot(String nroCuenta, String id);

	Boolean existsByNroCuentaCciAndIdNot(String nroCci, String id);
}
