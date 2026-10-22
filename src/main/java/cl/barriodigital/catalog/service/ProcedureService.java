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

@Service
public class ProcedureService {

    private final ProcedureRepository repository;

    public ProcedureService(ProcedureRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponse> list(boolean includeInactive) {
        List<ProcedureType> procedures = includeInactive
                ? repository.findAllByOrderByNameAsc()
                : repository.findByActiveTrueOrderByNameAsc();
        return procedures.stream().map(ProcedureResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProcedureResponse get(Long id, boolean includeInactive) {
        return ProcedureResponse.from(find(id, includeInactive));
    }

    @Transactional
    public ProcedureResponse create(ProcedureRequest request) {
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCase(name)) {
            throw new DuplicateProcedureException(name);
        }
        ProcedureType entity = new ProcedureType();
        apply(entity, request, name);
        return save(entity, name);
    }

    @Transactional
    public ProcedureResponse update(Long id, ProcedureRequest request) {
        ProcedureType entity = find(id, true);
        String name = request.name().trim();
        if (repository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateProcedureException(name);
        }
        apply(entity, request, name);
        return save(entity, name);
    }

    /** Baja lógica: el tipo deja de ofrecerse pero se conserva para las solicitudes existentes. */
    @Transactional
    public void deactivate(Long id) {
        find(id, true).setActive(false);
    }

    /** Reactiva un tipo dado de baja. Es idempotente: si ya estaba activo no cambia nada. */
    @Transactional
    public void activate(Long id) {
        find(id, true).setActive(true);
    }

    private ProcedureType find(Long id, boolean includeInactive) {
        return (includeInactive
                ? repository.findById(id)
                : repository.findByIdAndActiveTrue(id))
                .orElseThrow(() -> new ProcedureNotFoundException(id));
    }

    private void apply(ProcedureType entity, ProcedureRequest request, String name) {
        entity.setName(name);
        entity.setRequirements(request.requirements().trim());
        entity.setDailyQuota(request.dailyQuota());
    }

    private ProcedureResponse save(ProcedureType entity, String name) {
        try {
            return ProcedureResponse.from(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException ex) {
            // Carrera entre dos altas con el mismo nombre: la restricción UNIQUE de la BD es la última defensa.
            throw new DuplicateProcedureException(name);
        }
    }
}
