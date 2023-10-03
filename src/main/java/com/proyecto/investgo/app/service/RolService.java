package com.proyecto.investgo.app.service;

import java.util.List;

import com.proyecto.investgo.app.dto.RolDto;
import com.proyecto.investgo.app.entity.Rol;

public interface RolService {

	public RolDto buscarporId(Long id);

	public List<RolDto> listarRoles();

	public Rol insertarRol(Rol rol);
}