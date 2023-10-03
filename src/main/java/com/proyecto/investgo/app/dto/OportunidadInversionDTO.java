package com.proyecto.investgo.app.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OportunidadInversionDTO implements Serializable {
	private static final long serialVersionUID = -5789038762874790147L;

	private String codigo;
	private double rendimiento;
	private double tir;
	private Boolean enProceso;
	private BigDecimal monto;
	private BigDecimal montoRecaudado;
	private LocalDate fechaCaducidad;
	private AuditoriaDTO auditoria;
	private LocalDate fechaPago;
	private EmpresaDTO empresa;
}
