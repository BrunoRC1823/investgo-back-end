package com.proyecto.investgo.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.investgo.app.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, String> {

	Optional<Empresa> findByCodigo(String codigo);

	Page<Empresa> findAllByAuditoriaEnable(boolean enable, Pageable pageable);

	Page<Empresa> findAllByRazonSocialContainingAndAuditoriaEnable(String keyword, boolean enable, Pageable pageable);

	boolean existsByNombreAndIdNot(String nombre, String id);

	boolean existsByRucAndIdNot(String ruc, String id);

	boolean existsByRazonSocialAndIdNot(String rs, String id);

	boolean existsByDireccionAndIdNot(String dir, String id);

	boolean existsByCorreoAndIdNot(String correo, String id);

	boolean existsByNroCuentaBancariaAndIdNot(String numCB, String id);

}
