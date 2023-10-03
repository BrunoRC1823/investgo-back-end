package com.proyecto.investgo.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.entity.Riesgo;
import com.proyecto.investgo.app.repository.RiesgoRepository;
import com.proyecto.investgo.app.service.RiesgoService;

@Service
public class RiesgoServiceImpl implements RiesgoService {
	@Autowired
	RiesgoRepository repo;

	@Override
	public List<Riesgo> listarRiesgo() {
		return repo.findAll();
	}

	@Override
	public Optional<Riesgo> buscarRiesgoxId(int idRiesgo) {
		return repo.findById(idRiesgo);
	}

	@Override
	public Riesgo insetarRiesgo(Riesgo riesgo) {
		return repo.save(riesgo);
	}

}
