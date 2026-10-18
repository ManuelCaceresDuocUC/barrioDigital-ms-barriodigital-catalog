package cl.barriodigital.catalog.service;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.catalog.model.QuotaUsage;
import cl.barriodigital.catalog.repository.QuotaUsageRepository;

/**
 * Crea la fila del día (con 0 consumidos) en su propia transacción. Si dos llamadas
 * la crean a la vez, la restricción UNIQUE hace fallar a una: esa falla se revierte
 * aquí y no contamina la transacción de quien llama.
 */
@Component
public class QuotaRowInitializer {

    private final QuotaUsageRepository repository;

    public QuotaRowInitializer(QuotaUsageRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureRow(Long procedureId, LocalDate date) {
        if (!repository.existsByProcedureIdAndUsageDate(procedureId, date)) {
            repository.saveAndFlush(new QuotaUsage(procedureId, date));
        }
    }
}
