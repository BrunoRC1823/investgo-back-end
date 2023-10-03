package com.proyecto.investgo.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.entity.Banco;
import com.proyecto.investgo.app.repository.BancoRepository;
import com.proyecto.investgo.app.service.BancosService;

@Service
public class BancosServiceImpl implements BancosService {
	@Autowired
	BancoRepository bancoRepo;

	@Override
	public List<Banco> findAll() {
		return bancoRepo.findAll();
	}

}
