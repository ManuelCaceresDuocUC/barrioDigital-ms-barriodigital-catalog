package cl.barriodigital.catalog.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Cuerpo de error. Usa los mismos nombres de campo ({@code status}, {@code error},
 * {@code message}) que el error por defecto de Spring Boot, para que Angular lea
 * {@code error.message} igual en todos los servicios.
 *
 * <p>QUÉ ES: el JSON que se devuelve cuando algo falla (record inmutable). Lo arma GlobalExceptionHandler.
 */
// NON_NULL: los campos null (por ejemplo fieldErrors cuando no es error de validación) no se envían.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        // Solo en errores de validación: campo -> mensaje (ej.: "dailyQuota" -> "no puede ser negativo").
        Map<String, String> fieldErrors) {
}
