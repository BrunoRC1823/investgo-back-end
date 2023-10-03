package com.proyecto.investgo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.TipoTransaccion;

public interface TipoTransaccionRepository extends JpaRepository<TipoTransaccion, Long> {
	
}
