package com.proyecto.investgo.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.proyecto.investgo.app.dto.FacturaDTO;
import com.proyecto.investgo.app.entity.Factura;
import com.proyecto.investgo.app.utils.Paginador;

public interface FacturaService {

	public Boolean save(Factura fac);

	Boolean saveFactura(Factura fac);

	Boolean updateFactura(Factura fac);

	public Boolean delete(String id);

	public Optional<Factura> findById(String id);

	public Optional<Factura> findByCodigo(String cod);

	public Optional<FacturaDTO> findByCodigoDto(String cod);

	List<Factura> findAllByEmpresaIdAndAuditoriaEnable(String idEmp, boolean enable);

	List<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad);

	Page<Factura> findAllFacturasByOportunidadInversionCodigo(String codOportunidad, Paginador paginador);

	Page<FacturaDTO> findAllFacturasByOportunidadInversionCodigoDto(String codOportunidad, Paginador paginador);

	Page<FacturaDTO> findAllByFechaEmisionBetweenDto(LocalDate fechaInicio, LocalDate fechaFin, Paginador paginador);

	Page<FacturaDTO> findAllByEmpresaIdAndAuditoriaEnableDto(String idEmp, boolean enable, Paginador paginador);

}
