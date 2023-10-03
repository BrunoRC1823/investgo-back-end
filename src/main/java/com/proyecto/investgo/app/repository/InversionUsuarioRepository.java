package com.proyecto.investgo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.investgo.app.entity.InversionUsuario;

public interface InversionUsuarioRepository extends JpaRepository<InversionUsuario, String> {

	Optional<InversionUsuario> findByCodigoAndUsuarioId(String codigo, String idUSu);

	@Query("SELECT iu FROM InversionUsuario iu JOIN FETCH iu.oportunidadInversion oi JOIN FETCH iu.usuario u WHERE iu.oportunidadInversion.id = :oportunidadInversionId")
	List<InversionUsuario> findAllByOportunidadInversionIdWithOportunidadInversionAndUsuario(
			@Param("oportunidadInversionId") String oportunidadInversionId);

	Page<InversionUsuario> findByOportunidadInversionId(String idOpo, Pageable pageable);

	Page<InversionUsuario> findByUsuarioId(String idUsu, Pageable pageable);

	Page<InversionUsuario> findByUsuarioIdAndAuditoriaEnable(String idUsu, boolean enable, Pageable pageable);

	Page<InversionUsuario> findByOportunidadInversionIdAndUsuarioId(String idOpo, String idUsu, Pageable pageable);
}
