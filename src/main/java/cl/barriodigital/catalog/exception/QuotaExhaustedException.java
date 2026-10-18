package cl.barriodigital.catalog.exception;

public class QuotaExhaustedException extends RuntimeException {

    public QuotaExhaustedException(String message) {
        super(message);
    }
}
