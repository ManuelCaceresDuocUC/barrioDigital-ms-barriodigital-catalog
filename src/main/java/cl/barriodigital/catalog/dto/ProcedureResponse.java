package cl.barriodigital.catalog.dto;

import cl.barriodigital.catalog.model.ProcedureType;

/**
 * QUÉ ES: DTO de salida: lo que el API devuelve de un tipo de trámite (nunca se expone la entidad JPA directa).
 */
public record ProcedureResponse(
        Long id,
        String name,
        String requirements,
        Integer dailyQuota,
        boolean active) {

    // Método de fábrica: convierte la entidad de base de datos en este DTO.
    public static ProcedureResponse from(ProcedureType entity) {
        return new ProcedureResponse(
                entity.getId(),
                entity.getName(),
                entity.getRequirements(),
                entity.getDailyQuota(),
                entity.isActive());
    }
}
