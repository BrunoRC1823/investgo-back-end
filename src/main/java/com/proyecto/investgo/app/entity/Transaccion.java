package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
public class Transaccion implements Serializable {

	private static final long serialVersionUID = -5143848088007519793L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@NotNull
	@DecimalMin(value = "0.01", inclusive = true)
	@DecimalMax(value = "9999.99", inclusive = true)
	@Digits(integer = 4, fraction = 2)
	@Column(nullable = false, precision = 6, scale = 2)
	private BigDecimal monto;
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private CuentaBancaria cuentaBancaria;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private TipoTransaccion tipoTransaccion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Cartera cartera;
	@Embedded
	private Auditoria auditoria;

	public Transaccion() {
		this.auditoria = new Auditoria();
		this.tipoTransaccion = new TipoTransaccion();
	}

}
