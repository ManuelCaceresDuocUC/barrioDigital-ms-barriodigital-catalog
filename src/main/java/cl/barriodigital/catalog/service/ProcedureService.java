package cl.barriodigital.catalog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.barriodigital.catalog.model.ProcedureType;
import cl.barriodigital.catalog.repository.ProcedureRepository;

@Service
public class ProcedureService {

    @Autowired
    private ProcedureRepository repository;

    public List<ProcedureType> getAllProcedures() {
        return repository.findAll();
    }

    public ProcedureType createProcedure(ProcedureType procedure) {
        return repository.save(procedure);
    }

    public ProcedureType updateProcedure(Long id, ProcedureType updatedData) {
        return repository.findById(id)
                .map(existing -> {
                    // Solo actualizamos requisitos y cupo, tal como indica el caso
                    if(updatedData.getName() != null) existing.setName(updatedData.getName());
                    if(updatedData.getRequirements() != null) existing.setRequirements(updatedData.getRequirements());
                    if(updatedData.getDailyQuota() != null) existing.setDailyQuota(updatedData.getDailyQuota());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Trámite no encontrado con ID: " + id));
    }
}