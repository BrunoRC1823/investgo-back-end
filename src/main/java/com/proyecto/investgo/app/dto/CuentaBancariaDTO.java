package com.proyecto.investgo.app.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CuentaBancariaDTO implements Serializable {

	private static final long serialVersionUID = 8149374232900850244L;

	private String codigo;
	private String nroCuenta;
	private String nroCuentaCci;
	private String cvv;
	private String mes;
	private String year;
	private BigDecimal saldo;
	private BancoDTO banco;
	private MonedaDTO moneda;

}
