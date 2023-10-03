package com.proyecto.investgo.app.service.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.investgo.app.dto.RolDto;
import com.proyecto.investgo.app.entity.Rol;
import com.proyecto.investgo.app.repository.RolRepository;
import com.proyecto.investgo.app.service.RolService;

@Service
public class RolServiceImpl implements RolService {

	@Autowired
	private RolRepository repositorio;
	private ModelMapper modelMapper = new ModelMapper();

	@Override
	public RolDto buscarporId(Long id) {
		Optional<Rol> rol = repositorio.findById(id);
		if (rol.isEmpty()) {
			return null;
		}
		return modelMapper.map(rol.get(), RolDto.class);
	}

	@Override
	public List<RolDto> listarRoles() {
		List<Rol> roles = repositorio.findAll();
		return roles.stream().map(r -> modelMapper.map(r, RolDto.class)).toList();
	}

	@Override
	public Rol insertarRol(Rol rol) {
		return repositorio.save(rol);
	}
}
