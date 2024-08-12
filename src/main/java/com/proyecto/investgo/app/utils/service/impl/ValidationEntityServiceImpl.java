package com.proyecto.investgo.app.utils.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.proyecto.investgo.app.utils.service.IValidationEntityService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ValidationEntityServiceImpl implements IValidationEntityService {

	@Override
	public Map<String, Object> validar(BindingResult result) {
		Map<String, Object> salida = new HashMap<>();
		List<String> errors = new ArrayList<>();
		result.getFieldErrors()
				.forEach(e -> errors.add("Campo ".concat(e.getField()).concat(": ").concat(e.getDefaultMessage())));
		salida.put("errors", errors);
		return salida;
	}

}
