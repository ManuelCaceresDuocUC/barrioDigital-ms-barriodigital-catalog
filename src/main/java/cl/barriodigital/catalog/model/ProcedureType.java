package cl.barriodigital.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * QUÉ ES: entidad JPA; una fila de la tabla procedure_types = un tipo de trámite del catálogo
 * (por ejemplo "Poda de árboles"). Lombok genera getters, setters y el constructor vacío que exige JPA.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "procedure_types")
public class ProcedureType {

    // Límites de largo compartidos con las validaciones del DTO (así BD y API usan los mismos números).
    public static final int NAME_MAX = 100;
    public static final int REQUIREMENTS_MAX = 1000;

    // Clave primaria autogenerada por la base de datos (columna IDENTITY de Oracle).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true: la base de datos impide dos trámites con el mismo nombre (última defensa).
    @Column(nullable = false, unique = true, length = NAME_MAX)
    private String name;

    // Texto con los requisitos que debe cumplir el vecino.
    @Column(nullable = false, length = REQUIREMENTS_MAX)
    private String requirements;

    // Cantidad máxima de solicitudes que se pueden admitir por día.
    @Column(name = "daily_quota", nullable = false)
    private Integer dailyQuota;

    // Baja lógica: false = deja de ofrecerse, pero la fila se conserva para las solicitudes antiguas.
    @Column(nullable = false)
    private boolean active = true;
}
