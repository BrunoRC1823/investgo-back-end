package com.proyecto.investgo.app.service.impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.investgo.app.dto.IdentificadorDTO;
import com.proyecto.investgo.app.dto.TransaccionDTO;
import com.proyecto.investgo.app.entity.Cartera;
import com.proyecto.investgo.app.entity.CuentaBancaria;
import com.proyecto.investgo.app.entity.Transaccion;
import com.proyecto.investgo.app.repository.TransaccionRepository;
import com.proyecto.investgo.app.service.TransaccionService;
import com.proyecto.investgo.app.utils.DefaultValues;
import com.proyecto.investgo.app.utils.Paginador;
import com.proyecto.investgo.app.utils.Prefix;
import com.proyecto.investgo.app.utils.service.IGeneratorCodigoService;

@Service
public class TransaccionesServiceImpl implements TransaccionService {
	@Autowired
	private TransaccionRepository transRepo;
	@Autowired
	private IGeneratorCodigoService codGenService;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	@Transactional
	public Boolean save(Transaccion trans) {
		IdentificadorDTO identificador = codGenService.generate(Prefix.PREFIX_TRANSACCION.getPrefix(),
				(int) transRepo.count());
		modelMapper.map(identificador, trans);
		Transaccion newTrans = transRepo.save(trans);
		if (newTrans == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public Boolean saveDeposito(Transaccion trans, CuentaBancaria cta, Cartera cartera) {
		cta.subtractSaldo(trans.getMonto());
		cartera.addSaldo(trans.getMonto());
		trans.setCuentaBancaria(cta);
		trans.setCartera(cartera);
		trans.getTipoTransaccion().setId(DefaultValues.DEFAULT_DEPOSITO);
		return save(trans);
	}

	@Override
	@Transactional
	public Boolean saveRetiro(Transaccion trans, CuentaBancaria cta, Cartera cartera) {
		cta.setSaldo(cta.addSaldo(trans.getMonto()));
		cartera.setSaldo(cartera.subtractSaldo(trans.getMonto()));
		trans.setCuentaBancaria(cta);
		trans.setCartera(cartera);
		trans.getTipoTransaccion().setId(DefaultValues.DEFAULT_RETIRO);
		return save(trans);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Transaccion> findByCodigo(String codigo) {
		return transRepo.findByCodigo(codigo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<TransaccionDTO> findByCodigoDto(String codigo) {
		Optional<Transaccion> trans = transRepo.findByCodigo(codigo);
		if (trans.isEmpty()) {
			return Optional.empty();
		}
		TransaccionDTO trasnDto = modelMapper.map(trans.get(), TransaccionDTO.class);
		return Optional.ofNullable(trasnDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TransaccionDTO> findAllByCuentaBancariaIdOrderByCodigoDto(String idCuentaEmi, Paginador paginador) {
		Page<Transaccion> lista = transRepo.findAllByCuentaBancariaIdOrderByCodigo(idCuentaEmi,
				paginador.getPageable());
		return lista.map(t -> modelMapper.map(t, TransaccionDTO.class));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TransaccionDTO> findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigoDto(long idTipo,
			String idUsu, Paginador paginador) {
		Page<Transaccion> lista = transRepo.findAllByTipoTransaccionIdAndCuentaBancariaUsuarioIdOrderByCodigo(idTipo,
				idUsu, paginador.getPageable());
		return lista.map(t -> modelMapper.map(t, TransaccionDTO.class));
	}

	@Override
	public Page<TransaccionDTO> findAllByCuentaBancariaUsuarioId(String idUsu, Paginador paginador) {
		Page<Transaccion> lista = transRepo.findAllByCuentaBancariaUsuarioId(idUsu,paginador.getPageable());
		return lista.map(t -> modelMapper.map(t, TransaccionDTO.class));
	}

}
