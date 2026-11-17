package cl.barriodigital.catalog.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.barriodigital.catalog.model.QuotaUsage;

/**
 * QUÉ ES: repositorio del consumo diario de cupo. Contiene la consulta más importante del servicio:
 * el descuento atómico.
 */
public interface QuotaUsageRepository extends JpaRepository<QuotaUsage, Long> {

    // Fila de consumo de un trámite en un día (vacío si todavía nadie consumió ese día).
    Optional<QuotaUsage> findByProcedureIdAndUsageDate(Long procedureId, LocalDate usageDate);

    // ¿Existe ya la fila del día?
    boolean existsByProcedureIdAndUsageDate(Long procedureId, LocalDate usageDate);

    /**
     * Descuenta un cupo de forma atómica: la comparación con el cupo configurado y el
     * incremento ocurren en una sola sentencia, así dos llamadas simultáneas con un
     * cupo restante no pueden pasar las dos. Devuelve 1 si descontó, 0 si no quedaban.
     */
    // @Modifying: es un UPDATE (no un SELECT). clearAutomatically/flushAutomatically sincronizan el
    // contexto de persistencia de JPA para que las lecturas posteriores vean el valor nuevo.
    // nativeQuery: es SQL puro (no JPQL), porque usa una subconsulta sobre otra tabla.
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
