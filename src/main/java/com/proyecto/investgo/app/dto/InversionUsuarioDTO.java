package com.proyecto.investgo.app.dto;

import java.math.BigDecimal;

import com.proyecto.investgo.app.entity.Auditoria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InversionUsuarioDTO {
	private String codigo;
	private OportunidadInversionDTO oportunidadInversion;
	private BigDecimal montoInvertido;
	private BigDecimal ganancia;
	private Auditoria auditoria;
	private UsuarioDTO usuario;
}
