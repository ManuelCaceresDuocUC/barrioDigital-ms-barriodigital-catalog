package cl.barriodigital.catalog.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.barriodigital.catalog.dto.ApiError;

/**
 * Traduce excepciones a respuestas HTTP. No hay un handler genérico para
 * {@code Exception}: así los errores de seguridad (401/403) siguen su curso normal.
 *
 * <p>QUÉ ES: manejador global de errores (@RestControllerAdvice): si cualquier controlador lanza una de
 * estas excepciones, Spring ejecuta el método correspondiente y devuelve un ApiError con el código HTTP adecuado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trámite inexistente -> 404.
    @ExceptionHandler(ProcedureNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ProcedureNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    // Nombre repetido -> 409.
    @ExceptionHandler(DuplicateProcedureException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateProcedureException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // Cupo agotado o trámite desactivado -> 409.
    @ExceptionHandler(QuotaExhaustedException.class)
    public ResponseEntity<ApiError> handleQuotaExhausted(QuotaExhaustedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    // Falló alguna validación de @Valid -> 400 con el detalle de cada campo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        // putIfAbsent: si un campo falla varias reglas, se conserva solo el primer mensaje.
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        // Mensaje general = todos los mensajes unidos por "; " (el frontend muestra este texto).
        String message = String.join("; ", fieldErrors.values());
        return build(HttpStatus.BAD_REQUEST, message, fieldErrors);
    }

    // El cuerpo no es JSON válido (o no calza con el DTO) -> 400.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido", null);
    }

    // Construye la respuesta de error uniforme.
    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> fieldErrors) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
