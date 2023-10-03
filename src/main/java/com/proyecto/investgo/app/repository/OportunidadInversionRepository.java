package com.proyecto.investgo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.investgo.app.entity.OportunidadInversion;

public interface OportunidadInversionRepository extends JpaRepository<OportunidadInversion, String> {
	Optional<OportunidadInversion> findByCodigo(String codigo);

	@Query("SELECT DISTINCT oi FROM OportunidadInversion oi LEFT JOIN FETCH oi.facturas WHERE oi.auditoria.enable = true")
	List<OportunidadInversion> findAllByAuditoriaEnableTrueWithFacturas();

	@Query("SELECT DISTINCT oi FROM OportunidadInversion oi LEFT JOIN FETCH oi.facturas WHERE oi.pagado = false")
	List<OportunidadInversion> findAllByPagadoFalseWithFacturas();

	Optional<OportunidadInversion> findByCodigoAndAuditoriaEnable(String codigo, boolean enable);

	Page<OportunidadInversion> findAllByAuditoriaEnable(boolean enable, Pageable pageable);

	Page<OportunidadInversion> findAllByEmpresaCodigo(String codigo, Pageable pageable);

}
