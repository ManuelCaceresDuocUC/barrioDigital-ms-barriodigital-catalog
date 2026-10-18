package cl.barriodigital.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.catalog.dto.ProcedureRequest;
import cl.barriodigital.catalog.dto.ProcedureResponse;
import cl.barriodigital.catalog.service.ProcedureService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalog/procedures")
public class ProcedureController {

    private static final String ADMIN = "Admin";
    private static final String OPERADOR = "Operador";

    private final ProcedureService service;

    public ProcedureController(ProcedureService service) {
        this.service = service;
    }

    /** Admin y Operador ven todos los tipos; Cliente solo los activos. */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Admin','Operador','Cliente')")
    public List<ProcedureResponse> list(Authentication auth) {
        return service.list(canSeeInactive(auth));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Operador','Cliente')")
    public ProcedureResponse get(@PathVariable Long id, Authentication auth) {
        return service.get(id, canSeeInactive(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('Admin')")
    public ProcedureResponse create(@Valid @RequestBody ProcedureRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ProcedureResponse update(@PathVariable Long id, @Valid @RequestBody ProcedureRequest request) {
        return service.update(id, request);
    }

    /** Desactiva el tipo (baja lógica). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('Admin')")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }

    private boolean canSeeInactive(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> ADMIN.equals(a) || OPERADOR.equals(a));
    }
}
