package com.proyecto.investgo.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.entity.TipoTransaccion;
import com.proyecto.investgo.app.repository.TipoTransaccionRepository;
import com.proyecto.investgo.app.service.TipoTransaccionService;

@Service
public class TipoTransaccionServiceImpl implements TipoTransaccionService {
	@Autowired
	TipoTransaccionRepository tipoTransRepo;

	@Override
	public Optional<TipoTransaccion> findById(long id) {
		return tipoTransRepo.findById(id);
	}

	@Override
	public List<TipoTransaccion> findAll() {
		return tipoTransRepo.findAll();
	}

}
