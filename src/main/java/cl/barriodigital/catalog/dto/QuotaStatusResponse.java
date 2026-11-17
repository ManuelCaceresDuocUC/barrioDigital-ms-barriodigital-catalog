package cl.barriodigital.catalog.dto;

import java.time.LocalDate;

/**
 * QUÉ ES: DTO de salida con el estado del cupo de un trámite en un día.
 *
 * @param procedureId id del tipo de trámite
 * @param name        nombre del trámite
 * @param date        día al que corresponde el cupo (en hora de Santiago)
 * @param dailyQuota  cupo configurado por día
 * @param used        cupos ya consumidos hoy
 * @param available   cupos que quedan (dailyQuota - used, nunca negativo)
 */
public record QuotaStatusResponse(
        Long procedureId,
        String name,
        LocalDate date,
        int dailyQuota,
        int used,
        int available) {
}
