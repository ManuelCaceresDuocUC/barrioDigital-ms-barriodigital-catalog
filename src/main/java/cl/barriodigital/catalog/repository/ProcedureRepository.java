package cl.barriodigital.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.barriodigital.catalog.model.ProcedureType;

public interface ProcedureRepository extends JpaRepository<ProcedureType, Long> {

    List<ProcedureType> findAllByOrderByNameAsc();

    List<ProcedureType> findByActiveTrueOrderByNameAsc();

    Optional<ProcedureType> findByIdAndActiveTrue(Long id);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
