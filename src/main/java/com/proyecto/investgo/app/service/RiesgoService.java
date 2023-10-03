package com.proyecto.investgo.app.service;

import java.util.List;
import java.util.Optional;

import com.proyecto.investgo.app.entity.Riesgo;

public interface RiesgoService{	
	public abstract List<Riesgo> listarRiesgo();
	public abstract Optional<Riesgo> buscarRiesgoxId(int idRiesgo);
	public abstract Riesgo insetarRiesgo(Riesgo riesgo);
}