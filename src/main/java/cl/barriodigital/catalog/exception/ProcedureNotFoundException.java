package cl.barriodigital.catalog.exception;

/**
 * QUÉ ES: excepción de negocio "no existe el trámite con ese id". Se traduce a HTTP 404 en GlobalExceptionHandler.
 */
public class ProcedureNotFoundException extends RuntimeException {

    public ProcedureNotFoundException(Long id) {
        super("No existe el tipo de trámite con id " + id);
    }
}
