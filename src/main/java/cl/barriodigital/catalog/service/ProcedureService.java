package cl.barriodigital.catalog.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.barriodigital.catalog.dto.ProcedureRequest;
import cl.barriodigital.catalog.dto.ProcedureResponse;
import cl.barriodigital.catalog.exception.DuplicateProcedureException;
import cl.barriodigital.catalog.exception.ProcedureNotFoundException;
import cl.barriodigital.catalog.model.ProcedureType;
import cl.barriodigital.catalog.repository.ProcedureRepository;

/**
 * QUÉ ES: lógica de negocio del catálogo de trámites (crear, editar, listar, desactivar, reactivar).
 * Aplica las reglas: nombre único sin importar mayúsculas, baja lógica y visibilidad según el rol.
 */
@Service
public class ProcedureService {

    private final ProcedureRepository repository;

    public ProcedureService(ProcedureRepository repository) {
        this.repository = repository;
    }

    // Lista los trámites. includeInactive = true para Admin/Operador; false para Cliente (solo activos).
    @Transactional(readOnly = true)
    public List<ProcedureResponse> list(boolean includeInactive) {
        List<ProcedureType> procedures = includeInactive
                ? repository.findAllByOrderByNameAsc()
                : repository.findByActiveTrueOrderByNameAsc();
        // Convierte cada entidad en su DTO de respuesta.
        return procedures.stream().map(ProcedureResponse::from).toList();
    }

    // Obtiene un trámite por id; si no existe (o está inactivo y el usuario no puede verlo) lanza 404.
    @Transactional(readOnly = true)
    public ProcedureResponse get(Long id, boolean includeInactive) {
        return ProcedureResponse.from(find(id, includeInactive));
    }

    // Crea un trámite nuevo.
    @Transactional
    public ProcedureResponse create(ProcedureRequest request) {
        // trim() quita espacios sobrantes antes de comparar y guardar.
        String name = request.name().trim();
        // Regla: no puede haber dos trámites con el mismo nombre.
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateProcedureException(name);
        }
        ProcedureType entity = new ProcedureType();
        apply(entity, request, name);
        return save(entity, name);
    }

    // Edita un trámite existente (incluso si está inactivo).
    @Transactional
    public ProcedureResponse update(Long id, ProcedureRequest request) {
        ProcedureType entity = find(id, true);
        String name = request.name().trim();
        // El nombre debe ser único entre los demás trámites (se excluye el propio).
        if (repository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateProcedureException(name);
        }
        apply(entity, request, name);
        return save(entity, name);
    }

    /** Baja lógica: el tipo deja de ofrecerse pero se conserva para las solicitudes existentes. */
    // No hace falta llamar a save: JPA detecta el cambio en la entidad y lo guarda al terminar la transacción.
    @Transactional
    public void deactivate(Long id) {
        find(id, true).setActive(false);
    }

    /** Reactiva un tipo dado de baja. Es idempotente: si ya estaba activo no cambia nada. */
    @Transactional
    public void activate(Long id) {
        find(id, true).setActive(true);
    }

    // Busca por id; con includeInactive=false ignora los inactivos. Si no encuentra, lanza ProcedureNotFoundException (404).
    private ProcedureType find(Long id, boolean includeInactive) {
        return (includeInactive
                ? repository.findById(id)
                : repository.findByIdAndActiveTrue(id))
                .orElseThrow(() -> new ProcedureNotFoundException(id));
    }

    // Copia los datos editables del DTO a la entidad (el id y "active" los define el servidor).
    private void apply(ProcedureType entity, ProcedureRequest request, String name) {
        entity.setName(name);
        entity.setRequirements(request.requirements().trim());
        entity.setDailyQuota(request.dailyQuota());
    }

    // Guarda y fuerza el INSERT/UPDATE de inmediato (saveAndFlush) para detectar aquí una violación de unicidad.
    private ProcedureResponse save(ProcedureType entity, String name) {
        try {
            return ProcedureResponse.from(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            // Carrera entre dos altas con el mismo nombre: la restricción UNIQUE de la BD es la última defensa.
            throw new DuplicateProcedureException(name);
        }
    }
}
