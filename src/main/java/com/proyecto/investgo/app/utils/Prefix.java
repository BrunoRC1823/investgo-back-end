package com.proyecto.investgo.app.utils;

public enum Prefix {
	PREFIX_USUARIO("USU"), PREFIX_EMPRESA("EMP"), PREFIX_CUENTA_BANCARIA("CTB"), PREFIX_FACTURA("FAC"),
	PREFIX_OPORTUNIDAD_INVERSION("OPI"), PREFIX_TRANSACCION("TRS"), PREFIX_INVERSION("INV");

	private final String prefix;

	Prefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

}
