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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.proyecto.investgo.app.utils.Regexp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cuentas_bancarias")
@Getter
@Setter
@AllArgsConstructor
public class CuentaBancaria implements Serializable {

	private static final long serialVersionUID = 1644874561080847061L;

	@Id
	private String id;
	@Column(nullable = false, unique = true)
	private String codigo;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NRO_CUENTA_BANCARIA)
	@Size(max = 28, min = 28)
	@Column(length = 30, name = "nro_cuenta", nullable = false, unique = true)
	private String nroCuenta;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NRO_CCI)
	@Size(min = 23, max = 23)
	@Column(length = 25, name = "nro_cci", nullable = false, unique = true)
	private String nroCuentaCci;
	@Pattern(regexp = Regexp.PATTERN_NUMBER)
	@Size(min = 3, max = 3)
	@Column(length = 3)
	private String cvv;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_MES)
	@Column(length = 2, nullable = false)
	private String mes;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_YEAR)
	@Column(length = 4, nullable = false)
	private String year;
	@NotNull
	@DecimalMin(value = "0.00", inclusive = true)
	@DecimalMax(value = "999999.99", inclusive = true)
	@Digits(integer = 6, fraction = 2)
	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal saldo;
	@Embedded
	private Auditoria auditoria;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "banco_id", nullable = false)
	private Banco banco;
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "moneda_id", nullable = false)
	private Moneda moneda;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	public CuentaBancaria() {
		this.auditoria = new Auditoria();
	}

	public void addUsuario(String id) {
		this.usuario = new Usuario();
		this.usuario.setId(id);
	}

	public BigDecimal addSaldo(BigDecimal monto) {
		BigDecimal saldoAdd = this.saldo.add(monto);
		this.saldo = saldoAdd;
		return saldoAdd;
	}

	public BigDecimal subtractSaldo(BigDecimal monto) {
		BigDecimal saldoAdd = this.saldo.subtract(monto);
		this.saldo = saldoAdd;
		return saldoAdd;
	}
}
