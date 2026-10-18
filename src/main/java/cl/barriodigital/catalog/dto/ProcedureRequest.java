package cl.barriodigital.catalog.dto;

import cl.barriodigital.catalog.model.ProcedureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente puede enviar al crear o editar un tipo de trámite.
 * Excluye a propósito {@code id} y {@code active}: los define el servidor.
 */
public record ProcedureRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = ProcedureType.NAME_MAX, message = "El nombre admite hasta {max} caracteres")
        String name,

        @NotBlank(message = "Los requisitos son obligatorios")
        @Size(max = ProcedureType.REQUIREMENTS_MAX, message = "Los requisitos admiten hasta {max} caracteres")
        String requirements,

        @NotNull(message = "El cupo diario es obligatorio")
        @Min(value = 0, message = "El cupo diario no puede ser negativo")
        Integer dailyQuota) {
}
