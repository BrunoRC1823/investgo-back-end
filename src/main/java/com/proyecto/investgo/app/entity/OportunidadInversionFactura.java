package com.proyecto.investgo.app.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oportunidad_inversion_has_facturas")
@Setter
@Getter
@NoArgsConstructor
public class OportunidadInversionFactura implements Serializable {
	private static final long serialVersionUID = -833187856202303762L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oportunidad_inversion_id", nullable = false)
	private OportunidadInversion oportunidadInversion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "factura_id", nullable = false)
	private Factura factura;
}
