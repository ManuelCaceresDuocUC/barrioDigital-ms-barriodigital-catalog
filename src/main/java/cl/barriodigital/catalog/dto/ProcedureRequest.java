package cl.barriodigital.catalog.dto;

import cl.barriodigital.catalog.model.ProcedureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente puede enviar al crear o editar un tipo de trámite.
 * Excluye a propósito {@code id} y {@code active}: los define el servidor.
 *
 * <p>QUÉ ES: DTO de entrada con validaciones declarativas (Bean Validation). Se activan con @Valid en el
 * controlador; si alguna falla, se responde 400 con el mensaje indicado. Separar el DTO de la entidad evita
 * que un cliente modifique campos que no debería (técnica contra "mass assignment").
 */
public record ProcedureRequest(
        // No puede ser null, vacío ni solo espacios; máximo 100 caracteres ({max} se reemplaza por el límite).
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = ProcedureType.NAME_MAX, message = "El nombre admite hasta {max} caracteres")
        String name,

        @NotBlank(message = "Los requisitos son obligatorios")
        @Size(max = ProcedureType.REQUIREMENTS_MAX, message = "Los requisitos admiten hasta {max} caracteres")
        String requirements,

        // Integer (no int) para poder detectar que falta (@NotNull); 0 es válido (trámite sin cupo).
        @NotNull(message = "El cupo diario es obligatorio")
        @Min(value = 0, message = "El cupo diario no puede ser negativo")
        Integer dailyQuota) {
}
