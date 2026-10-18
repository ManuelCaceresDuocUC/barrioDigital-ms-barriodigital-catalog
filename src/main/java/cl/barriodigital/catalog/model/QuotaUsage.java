package cl.barriodigital.catalog.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Cupos consumidos de un tipo de trámite en un día. Una fila por tipo y día. */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "procedure_quota_usage",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quota_procedure_date",
                columnNames = { "procedure_id", "usage_date" }))
public class QuotaUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "procedure_id", nullable = false)
    private Long procedureId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "used_count", nullable = false)
    private int usedCount;

    public QuotaUsage(Long procedureId, LocalDate usageDate) {
        this.procedureId = procedureId;
        this.usageDate = usageDate;
        this.usedCount = 0;
    }
}
