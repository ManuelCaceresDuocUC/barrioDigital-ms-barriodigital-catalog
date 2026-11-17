package cl.barriodigital.catalog.service;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.catalog.dto.QuotaStatusResponse;
import cl.barriodigital.catalog.exception.ProcedureNotFoundException;
import cl.barriodigital.catalog.exception.QuotaExhaustedException;
import cl.barriodigital.catalog.model.ProcedureType;
import cl.barriodigital.catalog.model.QuotaUsage;
import cl.barriodigital.catalog.repository.ProcedureRepository;
import cl.barriodigital.catalog.repository.QuotaUsageRepository;

/**
 * QUÉ ES: lógica del cupo diario. Garantiza que NUNCA se admitan más solicitudes que el cupo, aunque
 * lleguen muchas llamadas al mismo tiempo (consumo atómico).
 */
@Service
public class QuotaService {

    private final ProcedureRepository procedures;
    private final QuotaUsageRepository usages;
    private final QuotaRowInitializer initializer;
    // Reloj inyectado (zona de Santiago): define qué es "hoy".
    private final Clock clock;

    public QuotaService(ProcedureRepository procedures, QuotaUsageRepository usages,
            QuotaRowInitializer initializer, Clock clock) {
        this.procedures = procedures;
        this.usages = usages;
        this.initializer = initializer;
        this.clock = clock;
    }

    // Consulta: cupo configurado, usado y disponible hoy.
    @Transactional(readOnly = true)
    public QuotaStatusResponse status(Long procedureId) {
        ProcedureType procedure = find(procedureId);
        return buildStatus(procedure, today());
    }

    /**
     * Descuenta un cupo del día. Un tipo en cupo 0, agotado o desactivado responde 409.
     */
    @Transactional
    public QuotaStatusResponse consume(Long procedureId) {
        ProcedureType procedure = find(procedureId);
        // Paso 1: un trámite desactivado no admite solicitudes nuevas.
        if (!procedure.isActive()) {
            throw new QuotaExhaustedException("El tipo de trámite '" + procedure.getName() + "' está desactivado");
        }

        LocalDate today = today();
        // Paso 2: asegura que exista la fila de hoy (con 0 consumidos).
        ensureRow(procedureId, today);

        // Paso 3: UPDATE atómico. Devuelve 0 filas si used_count ya alcanzó el cupo => se rechaza con 409.
        if (usages.consumeIfAvailable(procedureId, today) == 0) {
            throw new QuotaExhaustedException(
                    "No quedan cupos hoy para '" + procedure.getName() + "'");
        }
        // Paso 4: se devuelve el estado actualizado.
        return buildStatus(procedure, today);
    }

    // Crea la fila del día; si otra llamada la creó al mismo tiempo, el error de unicidad se ignora.
    private void ensureRow(Long procedureId, LocalDate date) {
        try {
            initializer.ensureRow(procedureId, date);
        } catch (DataIntegrityViolationException ex) {
            // Otra llamada creó la misma fila al mismo tiempo: es lo esperado, se sigue.
        }
    }

    // Arma la respuesta: si hoy nadie consumió, la fila no existe y "usado" es 0.
    private QuotaStatusResponse buildStatus(ProcedureType procedure, LocalDate date) {
        int used = usages.findByProcedureIdAndUsageDate(procedure.getId(), date)
                .map(QuotaUsage::getUsedCount)
                .orElse(0);
        int quota = procedure.getDailyQuota();
        // Math.max(0, ...) evita mostrar disponibles negativos si se bajó el cupo después de consumir.
        return new QuotaStatusResponse(
                procedure.getId(), procedure.getName(), date, quota, used, Math.max(0, quota - used));
    }

    // Busca el trámite o lanza 404.
    private ProcedureType find(Long id) {
        return procedures.findById(id).orElseThrow(() -> new ProcedureNotFoundException(id));
    }

    // Fecha de hoy según el reloj de la aplicación (hora de Santiago).
    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
