package cl.barriodigital.catalog.dto;

import cl.barriodigital.catalog.model.ProcedureType;

public record ProcedureResponse(
        Long id,
        String name,
        String requirements,
        Integer dailyQuota,
        boolean active) {

    public static ProcedureResponse from(ProcedureType entity) {
        return new ProcedureResponse(
                entity.getId(),
                entity.getName(),
                entity.getRequirements(),
                entity.getDailyQuota(),
                entity.isActive());
    }
}
