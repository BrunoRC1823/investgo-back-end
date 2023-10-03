package com.proyecto.investgo.app.dto;

import java.io.Serializable;
import java.util.Date;

import com.proyecto.investgo.app.entity.Riesgo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDTO implements Serializable {
	private static final long serialVersionUID = -6034359034944398572L;
	private String codigo;
	private String nomRepLegal;
	private String apeRepLegal;
	private String nombre;
	private String ruc;
	private String razonSocial;
	private Date fecIniActv;
	private String direccion;
	private String telefono;
	private String correo;
	private String sector;
	private Riesgo riesgo;
	private AuditoriaDTO auditoria;
	
}
