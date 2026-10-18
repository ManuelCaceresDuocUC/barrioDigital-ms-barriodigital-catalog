package cl.barriodigital.catalog.exception;

public class DuplicateProcedureException extends RuntimeException {

    public DuplicateProcedureException(String name) {
        super("Ya existe un tipo de trámite llamado '" + name + "'");
    }
}
