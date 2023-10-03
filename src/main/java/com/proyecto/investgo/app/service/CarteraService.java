package com.proyecto.investgo.app.service;

import java.util.Optional;

import com.proyecto.investgo.app.dto.CarteraDTO;
import com.proyecto.investgo.app.entity.Cartera;

public interface CarteraService {
	public Optional<Cartera> findByUsuarioId(String idUsu);

	public Optional<CarteraDTO> findByUsuarioIdDto(String idUsu);
}
