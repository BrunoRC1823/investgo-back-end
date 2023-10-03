package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Regexp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@AllArgsConstructor
public class Empresa implements Serializable {
	private static final long serialVersionUID = 6868040749400153080L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(name = "nombre_representante_legal", length = 45, nullable = false)
	private String nomRepLegal;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(name = "apellido_representante_legal", length = 45, nullable = false)
	private String apeRepLegal;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(name = "nombre_empresa", length = 45, nullable = false, unique = true)
	private String nombre;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NUMBER)
	@Size(max = 11, min = 11)
	@Column(length = 11, nullable = false, unique = true)
	private String ruc;
	@Pattern(regexp = Regexp.PATTERN_RAZON_SOCIAL)
	@Column(name = "razon_social", unique = true)
	private String razonSocial;
	@Column(name = "fecha_inicio_actv")
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
	private Date fecIniActv;
	@NotBlank
	@Size(max = 60, min = 5)
	@Column(length = 125, nullable = false, unique = true)
	private String direccion;
	@Pattern(regexp = Regexp.PATTERN_PHONE)
	@Column(length = 9)
	@Size(max = 9, min = 9)
	private String telefono;
	@NotBlank
	@Email
	@Size(max = 125, min = 5)
	@Column(length = 125, unique = true, nullable = false)
	private String correo;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NUMBER)
	@Size(max = 20, min = 20)
	@Column(name = "nro_cuenta_bancaria", nullable = false, unique = true)
	private String nroCuentaBancaria;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 45, min = 5)
	@Column(length = 45, nullable = false)
	private String sector;
	@Embedded
	private Auditoria auditoria;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Riesgo riesgo;
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, mappedBy = "empresa")
	private List<Factura> facturas;

	public Empresa() {
		this.facturas = new ArrayList<>();
		this.auditoria = new Auditoria();
	}

	@PrePersist
	public void prePersist() {
		this.riesgo = new Riesgo(DefaultValues.DEFAULT_ID_RIESGO);
	}
}