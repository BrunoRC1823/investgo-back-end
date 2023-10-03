package com.proyecto.investgo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.Moneda;

public interface MonedaRepository extends JpaRepository<Moneda, Integer> {

}
