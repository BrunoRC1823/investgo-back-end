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
public class UsuarioDTO implements Serializable {
	private static final long serialVersionUID = 3744213130842088632L;
	private String codigo;
	private String nombre;
	private String apellidoPa;
	private String apellidoMa;
	private String telefono;
	private String correo;
	private String dni;
	private String foto;
}