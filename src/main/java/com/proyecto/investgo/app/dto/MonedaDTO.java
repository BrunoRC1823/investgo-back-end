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
public class MonedaDTO implements Serializable {
	private static final long serialVersionUID = -2339301426801425865L;

	private String nombre;
	private String valor;
}
