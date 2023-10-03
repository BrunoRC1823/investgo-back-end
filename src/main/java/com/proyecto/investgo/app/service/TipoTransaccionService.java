package com.proyecto.investgo.app.service;

import java.util.List;
import java.util.Optional;

import com.proyecto.investgo.app.entity.TipoTransaccion;

public interface TipoTransaccionService {
	public List<TipoTransaccion> findAll();

	public Optional<TipoTransaccion> findById(long id);
}
