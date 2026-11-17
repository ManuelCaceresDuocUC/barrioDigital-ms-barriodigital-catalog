package cl.barriodigital.catalog.exception;

/**
 * QUÉ ES: excepción de negocio "no se pudo descontar cupo" (agotado o trámite desactivado).
 * Se traduce a HTTP 409. Es la señal que usa requests para saber que no puede admitir la solicitud.
 */
public class QuotaExhaustedException extends RuntimeException {

    public QuotaExhaustedException(String message) {
        super(message);
    }
}
