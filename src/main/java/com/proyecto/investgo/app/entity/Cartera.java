package com.proyecto.investgo.app.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carteras")
@Getter
@Setter
@NoArgsConstructor
public class Cartera implements Serializable {
	private static final long serialVersionUID = 8623531098763837755L;
	@Id
	private String id;
	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal saldo;
	@OneToOne
	@JoinColumn(name = "usuario_id", unique = true, nullable = false)
	private Usuario usuario;

	@PrePersist
	public void prePersist() {
		this.id = UUID.randomUUID().toString();
		this.saldo = new BigDecimal("0.0");
	}

	public BigDecimal addSaldo(BigDecimal monto) {
		BigDecimal saldoAdd = this.saldo.add(monto);
		this.saldo = saldoAdd;
		return saldoAdd;
	}

	public BigDecimal subtractSaldo(BigDecimal monto) {
		BigDecimal saldoAdd = this.saldo.subtract(monto);
		this.saldo = saldoAdd;
		return saldoAdd;
	}
}
