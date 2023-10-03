package com.proyecto.investgo.app.tasks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proyecto.investgo.app.entity.InversionUsuario;
import com.proyecto.investgo.app.entity.OportunidadInversion;
import com.proyecto.investgo.app.entity.Usuario;
import com.proyecto.investgo.app.service.InversionUsuarioService;
import com.proyecto.investgo.app.service.OportunidadInversionService;
import com.proyecto.investgo.app.service.UsuarioService;
import com.proyecto.investgo.app.utils.DefaultValues;

@Component
public class fechaVerificador {
	@Autowired
	private OportunidadInversionService opoService;
	@Autowired
	private InversionUsuarioService inverUsuService;
	@Autowired
	private UsuarioService usuarioService;
	private Logger log = LoggerFactory.getLogger(fechaVerificador.class);
	private final Semaphore semaphore = new Semaphore(1);

	@Scheduled(cron = DefaultValues.DEFAULT_CRON_DAILY)
	public void verificarFechasVencidas() {
		try {
			semaphore.acquire();
			log.info("Iniciando verificación de fechas vencidas...");
			LocalDate fechaActual = LocalDate.now();
			List<OportunidadInversion> listaOpo = opoService.findAllByAuditoriaEnableTrueWithFacturas();
			log.info("Buscando oportunidades......");
			listaOpo.forEach(opo -> {
				if (opo.getFechaCaducidad().isBefore(fechaActual)) {
					log.info("Buscando fechas vencidas......");
					opo.getAuditoria().setEnable(false);
					Boolean opoUpdate = opoService.save(opo);
					if (!opoUpdate) {
						log.info("Ocurrio un error, no se pudo cambiar el estado de la Oportunidad de inversion: "
								+ opo.getCodigo());
					}
					log.info("La oportunidad de inversion: " + opo.getCodigo() + " caduco!");
				}

			});
			log.info("Verificación de fechas vencidas terminada.");
		} catch (InterruptedException e) {
			log.error("Error al adquirir el semáforo: " + e.getMessage(), e);
		} finally {
			semaphore.release();
		}
	}

	@Scheduled(cron = DefaultValues.DEFAULT_CRON_DAILY)
	public void verificarFechaPagoVencidas() {
		try {
			if (semaphore.tryAcquire()) {
				log.info("Iniciando verificación de fechas de pago vencidas...");
				LocalDate fechaActual = LocalDate.now();
				List<OportunidadInversion> listaOpo = opoService.findAllByPagadoFalseWithFacturas();
				log.info("Buscando oportunidades......");
				listaOpo.forEach(opo -> {
					if (opo.getFechaPago().isBefore(fechaActual)
							&& opo.getMontoRecaudado().compareTo(BigDecimal.ZERO) > 0) {
						List<InversionUsuario> listaInver = inverUsuService
								.findAllByOportunidadInversionIdWithOportunidadInversionAndUsuario(opo.getId());
						log.info("Buscando fechas de pago vencidas......");
						listaInver.forEach(inver -> {
							Optional<Usuario> usuario = usuarioService.findById(inver.getUsuario().getId());
							usuario.get().getCartera().addSaldo(inver.getGanancia());
							usuarioService.save(usuario.get());
							inver.getAuditoria().setEnable(false);
							inverUsuService.save(inver);
							log.info("El cliente: " + opo.getCodigo() + ", recibio un pago de :" + inver.getGanancia());
						});
						opo.setPagado(true);
						opoService.save(opo);
						log.info("Se realizaron los pagos correspondientes de la oportunidad: " + opo.getCodigo());
					}
				});
				log.info("Verificación de fechas de pago vencidas terminada.");
			} else {
				log.info("La cola esta llena, esperando...");
			}
		} catch (Exception e) {
			log.error("Error al verificar fechas de pago vencidas: " + e.getMessage(), e);
		} finally {
			semaphore.release();
		}
	}
}
