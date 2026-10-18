package cl.barriodigital.catalog.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.barriodigital.catalog.model.QuotaUsage;

public interface QuotaUsageRepository extends JpaRepository<QuotaUsage, Long> {

    Optional<QuotaUsage> findByProcedureIdAndUsageDate(Long procedureId, LocalDate usageDate);

    boolean existsByProcedureIdAndUsageDate(Long procedureId, LocalDate usageDate);

    /**
     * Descuenta un cupo de forma atómica: la comparación con el cupo configurado y el
     * incremento ocurren en una sola sentencia, así dos llamadas simultáneas con un
     * cupo restante no pueden pasar las dos. Devuelve 1 si descontó, 0 si no quedaban.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = """
            UPDATE procedure_quota_usage
               SET used_count = used_count + 1
             WHERE procedure_id = :procedureId
               AND usage_date = :usageDate
               AND used_count < (SELECT daily_quota FROM procedure_types WHERE id = :procedureId)
            """)
    int consumeIfAvailable(@Param("procedureId") Long procedureId, @Param("usageDate") LocalDate usageDate);
}
