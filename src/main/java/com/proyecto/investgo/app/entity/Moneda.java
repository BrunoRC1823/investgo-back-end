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
@Table(name = "monedas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Moneda implements Serializable {
	
	private static final long serialVersionUID = 3290990299786933707L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String nombre;

	private String valor;
}
