package com.proyecto.investgo.app.utils.service;

import java.util.Map;

import org.springframework.validation.BindingResult;

public interface IValidationEntityService {
	Map<String, Object> validar(BindingResult result);
}
