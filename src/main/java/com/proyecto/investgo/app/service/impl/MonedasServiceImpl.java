package com.proyecto.investgo.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.entity.Moneda;
import com.proyecto.investgo.app.repository.MonedaRepository;
import com.proyecto.investgo.app.service.MonedasService;

@Service
public class MonedasServiceImpl implements MonedasService {

	@Autowired
	MonedaRepository monedaRepo;

	@Override
	public List<Moneda> findAll() {
		return monedaRepo.findAll();
	}

}
