package com.proyecto.investgo.app.entity;

import java.io.Serializable;

import javax.persistence.Column;
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
@Table(name = "bancos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Banco implements Serializable {

	private static final long serialVersionUID = -8496301483234956917L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 20, unique = true, nullable = false)
	private String nombre;

}
