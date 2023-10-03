package com.proyecto.investgo.app.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolDto implements Serializable{
	private static final long serialVersionUID = 7394747084576921392L;
	private Long id;
	private String rol;
}
