package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facturas")
@Getter
@Setter
public class Factura implements Serializable {

	private static final long serialVersionUID = -5677858371478643791L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@NotNull
	@Size(min = 4, max = 60)
	@Column(length = 60, nullable = false)
	private String descripcion;
	@NotNull
	@DecimalMin(value = "0.00", inclusive = false)
	@DecimalMax(value = "999999.99", inclusive = true)
	@Digits(integer = 6, fraction = 2)
	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal monto;
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
	@Column(name = "fecha_emision", nullable = false)
	private LocalDate fechaEmision;
	private Auditoria auditoria;
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Empresa empresa;

	public Factura() {
		this.empresa = new Empresa();
		this.auditoria = new Auditoria();
	}

}
