package com.proyecto.investgo.app.service.impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.investgo.app.dto.CarteraDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.repository.CarteraRespository;
import com.proyecto.investgo.app.service.CarteraService;

@Service
public class CarteraServiceImpl implements CarteraService {

	@Autowired
	private CarteraRespository carteraRepo;
	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional(readOnly = true)
	public Optional<Cartera> findByUsuarioId(String idUsu) {
		return carteraRepo.findByUsuarioId(idUsu);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CarteraDTO> findByUsuarioIdDto(String idUsu) {
		Optional<Cartera> cartera = carteraRepo.findByUsuarioId(idUsu);
		if (cartera.isEmpty()) {
			return Optional.empty();
		}
		CarteraDTO carteraDto = modelMapper.map(cartera.get(), CarteraDTO.class);
		return Optional.ofNullable(carteraDto);
	}

}
