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
 *
 * <p>QUÉ ES: clase auxiliar de QuotaService. Está separada a propósito: @Transactional solo funciona al
 * llamar a otro bean (un método que se llama a sí mismo dentro de la misma clase ignora la anotación).
 */
@Component
public class QuotaRowInitializer {

    private final QuotaUsageRepository repository;

    public QuotaRowInitializer(QuotaUsageRepository repository) {
        this.repository = repository;
    }

    // REQUIRES_NEW: abre una transacción nueva e independiente, suspendiendo la de quien llama.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureRow(Long procedureId, LocalDate date) {
        // Solo crea la fila si no existe todavía para ese trámite y día.
        if (!repository.existsByProcedureIdAndUsageDate(procedureId, date)) {
            repository.saveAndFlush(new QuotaUsage(procedureId, date));
        }
    }
}
