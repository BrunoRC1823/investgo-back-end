package com.proyecto.investgo.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.investgo.app.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

	@Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.id = :id")
	Optional<Usuario> findById(String id);

	@Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.username = :username")
	Optional<Usuario> findByUsernameWithRoles(String username);

	Optional<Usuario> findByCodigo(String codigo);

	Page<Usuario> findAllByAuditoriaEnable(boolean enable, Pageable pageable);

	@Query(value = "SELECT u FROM Usuario u JOIN FETCH u.rol", countQuery = "SELECT COUNT(u) FROM Usuario u")
	Page<Usuario> findAllWithRoles(Pageable pageable);

	boolean existsByUsernameAndIdNot(String username, String id);

	boolean existsByCorreoAndIdNot(String correo, String id);

	boolean existsByDniAndIdNot(String dni, String id);
}
