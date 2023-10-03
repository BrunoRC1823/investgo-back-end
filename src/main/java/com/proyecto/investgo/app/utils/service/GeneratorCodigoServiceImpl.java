package com.proyecto.investgo.app.utils.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.dto.IdentificadorDTO;

@Service
public class GeneratorCodigoServiceImpl implements IGeneratorCodigoService {

	@Override
	public IdentificadorDTO generate(String prefix, int count) {
		IdentificadorDTO dto = new IdentificadorDTO();
		String format = "%s%06d";
		UUID uuid = UUID.randomUUID();
		dto.setId(uuid.toString());
		dto.setCodigo(String.format(format, prefix, count + 1));
		return dto;
	}

}
