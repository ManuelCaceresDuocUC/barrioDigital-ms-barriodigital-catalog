package cl.barriodigital.catalog.dto;

import java.time.LocalDate;

public record QuotaStatusResponse(
        Long procedureId,
        String name,
        LocalDate date,
        int dailyQuota,
        int used,
        int available) {
}
