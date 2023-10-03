package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Auditoria implements Serializable {
	private static final long serialVersionUID = 4357050162907166016L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE, dd-MM-yyyy, h:mm:ss a", timezone = "America/Lima")
	@Column(nullable = false)
	private LocalDateTime fecha;
	@Column(columnDefinition = "TINYINT(1)", nullable = false)
	private Boolean enable;

	@PrePersist
	public void prePersist() {
		this.fecha = LocalDateTime.now();
		this.enable = true;
	}
}
