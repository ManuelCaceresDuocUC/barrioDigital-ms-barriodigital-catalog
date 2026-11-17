package cl.barriodigital.catalog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.barriodigital.catalog.model.ProcedureType;

/**
 * QUÉ ES: repositorio Spring Data JPA de los tipos de trámite. Spring genera el SQL de cada método
 * a partir de su nombre; JpaRepository aporta save, findById, findAll, etc.
 */
public interface ProcedureRepository extends JpaRepository<ProcedureType, Long> {

    // Todos (activos e inactivos) ordenados por nombre: vista del personal.
    List<ProcedureType> findAllByOrderByNameAsc();

    // Solo los activos, ordenados por nombre: vista del Cliente.
    List<ProcedureType> findByActiveTrueOrderByNameAsc();

    // Un trámite por id, solo si está activo.
    Optional<ProcedureType> findByIdAndActiveTrue(Long id);

    // ¿Existe ya ese nombre? (sin distinguir mayúsculas): para evitar duplicados al crear.
    boolean existsByNameIgnoreCase(String name);

    // Igual, pero excluyendo el propio id: para permitir "editar sin cambiar el nombre".
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
