package cl.barriodigital.catalog.exception;

public class ProcedureNotFoundException extends RuntimeException {

    public ProcedureNotFoundException(Long id) {
        super("No existe el tipo de trámite con id " + id);
    }
}
