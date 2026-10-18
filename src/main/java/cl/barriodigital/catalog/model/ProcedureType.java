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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "procedure_types")
public class ProcedureType {

    public static final int NAME_MAX = 100;
    public static final int REQUIREMENTS_MAX = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = NAME_MAX)
    private String name;

    @Column(nullable = false, length = REQUIREMENTS_MAX)
    private String requirements;

    @Column(name = "daily_quota", nullable = false)
    private Integer dailyQuota;

    @Column(nullable = false)
    private boolean active = true;
}
