package com.proyecto.investgo.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.Cartera;

public interface CarteraRespository extends JpaRepository<Cartera, String> {
	public Optional<Cartera> findByUsuarioId(String idUsu);

}
