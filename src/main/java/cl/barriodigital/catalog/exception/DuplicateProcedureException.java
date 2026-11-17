package cl.barriodigital.catalog.exception;

/**
 * QUÉ ES: excepción de negocio "ya existe un trámite con ese nombre". Se traduce a HTTP 409 (Conflict)
 * en GlobalExceptionHandler. Extiende RuntimeException: es "no comprobada", no obliga a declarar throws.
 */
public class DuplicateProcedureException extends RuntimeException {

    public DuplicateProcedureException(String name) {
        super("Ya existe un tipo de trámite llamado '" + name + "'");
    }
}
