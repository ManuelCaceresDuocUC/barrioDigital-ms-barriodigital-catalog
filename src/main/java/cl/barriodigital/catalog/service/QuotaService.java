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

@Service
public class QuotaService {

    private final ProcedureRepository procedures;
    private final QuotaUsageRepository usages;
    private final QuotaRowInitializer initializer;
    private final Clock clock;

    public QuotaService(ProcedureRepository procedures, QuotaUsageRepository usages,
            QuotaRowInitializer initializer, Clock clock) {
        this.procedures = procedures;
        this.usages = usages;
        this.initializer = initializer;
        this.clock = clock;
    }

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
        if (!procedure.isActive()) {
            throw new QuotaExhaustedException("El tipo de trámite '" + procedure.getName() + "' está desactivado");
        }

        LocalDate today = today();
        ensureRow(procedureId, today);

        if (usages.consumeIfAvailable(procedureId, today) == 0) {
            throw new QuotaExhaustedException(
                    "No quedan cupos hoy para '" + procedure.getName() + "'");
        }
        return buildStatus(procedure, today);
    }

    private void ensureRow(Long procedureId, LocalDate date) {
        try {
            initializer.ensureRow(procedureId, date);
        } catch (DataIntegrityViolationException ex) {
            // Otra llamada creó la misma fila al mismo tiempo: es lo esperado, se sigue.
        }
    }

    private QuotaStatusResponse buildStatus(ProcedureType procedure, LocalDate date) {
        int used = usages.findByProcedureIdAndUsageDate(procedure.getId(), date)
                .map(QuotaUsage::getUsedCount)
                .orElse(0);
        int quota = procedure.getDailyQuota();
        return new QuotaStatusResponse(
                procedure.getId(), procedure.getName(), date, quota, used, Math.max(0, quota - used));
    }

    private ProcedureType find(Long id) {
        return procedures.findById(id).orElseThrow(() -> new ProcedureNotFoundException(id));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
