package com.proyecto.investgo.app.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityMixin {

	@JsonCreator
	protected SimpleGrantedAuthorityMixin(@JsonProperty("authority") String role) {
	}

}
