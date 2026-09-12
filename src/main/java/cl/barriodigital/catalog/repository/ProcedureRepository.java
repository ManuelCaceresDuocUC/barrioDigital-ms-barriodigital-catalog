package cl.barriodigital.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.barriodigital.catalog.model.ProcedureType;

@Repository
public interface ProcedureRepository extends JpaRepository<ProcedureType, Long> {
}