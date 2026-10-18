package cl.barriodigital.catalog.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Cuerpo de error. Usa los mismos nombres de campo ({@code status}, {@code error},
 * {@code message}) que el error por defecto de Spring Boot, para que Angular lea
 * {@code error.message} igual en todos los servicios.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors) {
}
