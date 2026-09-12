package cl.barriodigital.catalog.model;

import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok genera automáticamente Getters, Setters y constructores
@Entity
@Table(name = "procedure_types")
public class ProcedureType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String requirements;

    @Column(name = "daily_quota", nullable = false)
    private Integer dailyQuota;
}