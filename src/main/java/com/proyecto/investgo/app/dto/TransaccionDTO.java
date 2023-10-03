package com.proyecto.investgo.app.dto;

import java.math.BigDecimal;

import com.proyecto.investgo.app.entity.TipoTransaccion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransaccionDTO {
	private String codigo;
	private BigDecimal monto;
	private CuentaBancariaDTO cuentaBancaria;
	private TipoTransaccion tipoTransaccion;
}
