package cl.barriodigital.catalog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import cl.barriodigital.catalog.exception.ProcedureNotFoundException;
import cl.barriodigital.catalog.model.ProcedureType;
import cl.barriodigital.catalog.repository.ProcedureRepository;

/** Baja y alta lógica de los tipos de trámite. */
class ProcedureServiceTest {

    private final ProcedureRepository repository = mock(ProcedureRepository.class);
    private final ProcedureService service = new ProcedureService(repository);

    private static ProcedureType procedure(boolean active) {
        ProcedureType p = new ProcedureType();
        p.setName("Poda de árbol");
        p.setRequirements("Dirección exacta");
        p.setDailyQuota(5);
        p.setActive(active);
        return p;
    }

    @Test
    void activarReactivaUnTipoDesactivado() {
        ProcedureType inactivo = procedure(false);
        when(repository.findById(3L)).thenReturn(Optional.of(inactivo));

        service.activate(3L);

        assertThat(inactivo.isActive()).isTrue();
    }

    @Test
    void activarUnTipoYaActivoEsIdempotente() {
        ProcedureType activo = procedure(true);
        when(repository.findById(3L)).thenReturn(Optional.of(activo));

        service.activate(3L);

        assertThat(activo.isActive()).isTrue();
    }

    @Test
    void activarUnTipoInexistenteLanzaNoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activate(99L)).isInstanceOf(ProcedureNotFoundException.class);
    }

    @Test
    void desactivarYVolverAActivarDejaElTipoComoAntes() {
        ProcedureType tipo = procedure(true);
        when(repository.findById(3L)).thenReturn(Optional.of(tipo));

        service.deactivate(3L);
        assertThat(tipo.isActive()).isFalse();

        service.activate(3L);
        assertThat(tipo.isActive()).isTrue();
        assertThat(tipo.getName()).isEqualTo("Poda de árbol");
        assertThat(tipo.getDailyQuota()).isEqualTo(5);
    }
}
