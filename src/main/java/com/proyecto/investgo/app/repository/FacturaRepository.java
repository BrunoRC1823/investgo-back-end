package com.proyecto.investgo.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.investgo.app.entity.Factura;

public interface FacturaRepository extends JpaRepository<Factura, String> {

	Optional<Factura> findByCodigo(String codFactura);

	List<Factura> findAllByEmpresaIdAndAuditoriaEnable(String idEmp, boolean enable);

	Page<Factura> findAllByEmpresaIdAndAuditoriaEnable(String idEmp, boolean enable, Pageable pageable);

	Page<Factura> findAllByFechaEmisionBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

	@Query("SELECT oif.factura FROM OportunidadInversionFactura oif WHERE oif.oportunidadInversion.codigo = :codOportunidad")
	List<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad);

	@Query(value = "SELECT oif.factura FROM OportunidadInversionFactura oif WHERE oif.oportunidadInversion.codigo = :codOportunidad", countQuery = "SELECT COUNT(oif) FROM OportunidadInversionFactura oif")
	Page<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad, Pageable pageable);
}
