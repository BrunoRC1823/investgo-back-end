package com.proyecto.investgo.app.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IdentificadorDTO implements Serializable{
	private static final long serialVersionUID = 2659110637579745341L;
	private String id;
	private String codigo;

}
