package com.proyecto.investgo.app.utils.service;

import com.proyecto.investgo.app.dto.IdentificadorDTO;

public interface IGeneratorCodigoService {
	public IdentificadorDTO generate(String prefix, int count);
}
