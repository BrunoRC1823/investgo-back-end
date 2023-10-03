package com.proyecto.investgo.app.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacturaDTO implements Serializable {

	private static final long serialVersionUID = -7400861870122993168L;
	private String codigo;
	private String descripcion;
	private BigDecimal monto;
	private LocalDate fechaEmision;
	private AuditoriaDTO auditoria;
}
