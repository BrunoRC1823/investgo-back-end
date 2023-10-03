package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
@Table(name = "inversiones_usuarios")
@Getter
@Setter
public class InversionUsuario implements Serializable {
	private static final long serialVersionUID = -432313609604880476L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private OportunidadInversion oportunidadInversion;
	@NotNull
	@DecimalMin(value = "0.01", inclusive = true)
	@DecimalMax(value = "9999.99", inclusive = true)
	@Digits(integer = 4, fraction = 2)
	@Column(name = "monto_invertido", nullable = false, precision = 6, scale = 2)
	private BigDecimal montoInvertido;
	@Column(nullable = false, precision = 7, scale = 2)
	private BigDecimal ganancia;
	@Embedded
	private Auditoria auditoria;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Usuario usuario;

	public InversionUsuario() {
		this.auditoria = new Auditoria();
	}

	public BigDecimal valorFuturoInversion() {
		BigDecimal valorTir = this.oportunidadInversion.getTir().add(new BigDecimal("1"));
		return this.montoInvertido.multiply(valorTir);
	}

	public BigDecimal valorRendimiento() {
		BigDecimal valorRendimiento = this.oportunidadInversion.getRendimiento();
		return this.montoInvertido.add(valorRendimiento);
	}

	public BigDecimal calcularGanancia() {
		BigDecimal valorSuma = valorRendimiento().add(valorFuturoInversion());
		return valorSuma.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
	}
}
