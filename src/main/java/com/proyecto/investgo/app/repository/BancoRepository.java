package com.proyecto.investgo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.Banco;

public interface BancoRepository extends JpaRepository<Banco, Integer> {

}
