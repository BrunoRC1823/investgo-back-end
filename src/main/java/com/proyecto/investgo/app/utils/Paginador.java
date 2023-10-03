package com.proyecto.investgo.app.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Paginador {
	private Integer pagina;
	private Integer elementosPagina;
	private String ordenadoPor;
	private String enOrden;

	public Pageable getPageable() {
		Sort sort = Sort.by(Sort.Direction.fromString(this.enOrden), this.ordenadoPor);
		return PageRequest.of(this.pagina, this.elementosPagina, sort);
	}

	public Paginador() {
		this.pagina = DefaultValues.DEFAULT_PAGINA;
		this.elementosPagina = DefaultValues.DEFAULT_ELEMENTOS_PAGINA;
		this.ordenadoPor = DefaultValues.DEFAULT_ORDENADOR_POR;
		this.enOrden = DefaultValues.DEFAULT_EN_ORDEN;
	}

}
