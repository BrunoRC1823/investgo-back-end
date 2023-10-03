package com.proyecto.investgo.app.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "riesgos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Riesgo implements Serializable {
	private static final long serialVersionUID = 2363921795084236311L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String rango;
	private String descripcion;

	public Riesgo(Long id) {
		this.id = id;
	}

}