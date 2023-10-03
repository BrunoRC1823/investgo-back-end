package com.proyecto.investgo.app.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BancoDTO implements Serializable{

	private static final long serialVersionUID = -2372108212214102643L;
	private String nombre;

}
