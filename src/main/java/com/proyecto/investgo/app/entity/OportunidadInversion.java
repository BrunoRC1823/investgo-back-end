package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "oportunidad_inversiones")
@Getter
@Setter
public class OportunidadInversion implements Serializable {

	private static final long serialVersionUID = 449232855345568535L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@Column(name = "en_proceso", columnDefinition = "TINYINT(1)", nullable = false)
	private Boolean enProceso;
	@Column(columnDefinition = "TINYINT(1)", nullable = false)
	private Boolean pagado;
	@Column(columnDefinition = "TINYINT(1)", nullable = false)
	private Boolean terminado;
	@NotNull
	@DecimalMin(value = "0.01", inclusive = true)
	@DecimalMax(value = "1.20", inclusive = true)
	@Digits(integer = 3, fraction = 2)
	@Column(precision = 5, scale = 2)
	private BigDecimal rendimiento;
	@NotNull
	@DecimalMin(value = "0.01", inclusive = true)
	@DecimalMax(value = "1.20", inclusive = true)
	@Digits(integer = 3, fraction = 2)
	@Column(precision = 5, scale = 2)
	private BigDecimal tir;
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal monto;
	@Column(name = "monto_recaudado", nullable = false, precision = 10, scale = 2)
	private BigDecimal montoRecaudado;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
	@Column(name = "fecha_caducidad", nullable = false)
	private LocalDate fechaCaducidad;
	@Embedded
	private Auditoria auditoria;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
	@Column(name = "fecha_pago", nullable = false)
	private LocalDate fechaPago;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Empresa empresa;
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "oportunidad_inversion_has_facturas", joinColumns = @JoinColumn(name = "oportunidad_inversion_id"), inverseJoinColumns = @JoinColumn(name = "factura_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"factura_id" }))
	private List<Factura> facturas;

	public OportunidadInversion() {
		this.facturas = new ArrayList<>();
		this.auditoria = new Auditoria();
	}

	public void addFactura(Factura factura) {
		this.facturas.add(factura);
	}

	@PrePersist
	public void prePersist() {
		this.montoRecaudado = new BigDecimal("0.0");
		this.enProceso = false;
		this.pagado = false;
		this.terminado = false;
	}
}