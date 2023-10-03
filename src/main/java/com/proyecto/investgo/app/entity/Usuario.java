package com.proyecto.investgo.app.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.investgo.app.utils.Regexp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
public class Usuario implements Serializable {
	private static final long serialVersionUID = 926387825632762561L;
	@Id
	private String id;
	@Column(length = 20, nullable = false, unique = true)
	private String codigo;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(length = 35, nullable = false)
	private String nombre;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(length = 45, nullable = false)
	private String apellidoPa;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_NAME_LASTNAME)
	@Size(max = 35, min = 2)
	@Column(length = 45, nullable = false)
	private String apellidoMa;
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
	@Size(max = 45, min = 2)
	@Column(length = 45, unique = true, nullable = false)
	private String username;
	@NotBlank
	@Pattern(regexp = Regexp.PATTERN_PASSWORD)
	@Size(max = 255, min = 6)
	@Column(length = 255, nullable = false)
	private String password;
	@Pattern(regexp = Regexp.PATTERN_NUMBER)
	@Column(length = 8, unique = true, nullable = false)
	@Size(min = 8, max = 8)
	private String dni;
	@Column(length = 255)
	private String foto;
	@Embedded
	private Auditoria auditoria;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id", nullable = false)
	private Rol rol;
	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "usuario")
	@JoinColumn(nullable = false)
	private Cartera cartera;

	public Usuario() {
		this.auditoria = new Auditoria();
	}

	public void addCartera(Cartera cartera) {
		this.cartera = cartera;
		cartera.setUsuario(this);
	}

}