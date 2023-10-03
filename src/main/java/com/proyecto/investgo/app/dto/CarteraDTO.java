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
public class CarteraDTO implements Serializable {
	private static final long serialVersionUID = 2645207456652864220L;
	private BigDecimal saldo;
}
