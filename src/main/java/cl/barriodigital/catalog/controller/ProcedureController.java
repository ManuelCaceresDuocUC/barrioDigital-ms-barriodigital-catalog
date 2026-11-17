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

/**
 * QUÉ ES: API REST del catálogo de tipos de trámite (CRUD) en /api/catalog/procedures.
 *
 * PERMISOS (rol del JWT): leer = Admin, Operador y Cliente; crear, editar, desactivar y reactivar = solo Admin.
 * El controlador solo traduce HTTP <-> servicio; las reglas están en ProcedureService.
 */
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
    // GET /api/catalog/procedures
    @GetMapping
    @PreAuthorize("hasAnyAuthority('Admin','Operador','Cliente')")
    public List<ProcedureResponse> list(Authentication auth) {
        // "auth" = usuario autenticado (Spring lo inyecta); se usa para decidir si ve los inactivos.
        return service.list(canSeeInactive(auth));
    }

    // GET /api/catalog/procedures/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Admin','Operador','Cliente')")
    public ProcedureResponse get(@PathVariable Long id, Authentication auth) {
        return service.get(id, canSeeInactive(auth));
    }

    // POST /api/catalog/procedures -> 201 Created. @Valid ejecuta las validaciones del DTO (nombre, cupo...).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('Admin')")
    public ProcedureResponse create(@Valid @RequestBody ProcedureRequest request) {
        return service.create(request);
    }

    // PUT /api/catalog/procedures/{id} -> edita nombre, requisitos y cupo diario.
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public ProcedureResponse update(@PathVariable Long id, @Valid @RequestBody ProcedureRequest request) {
        return service.update(id, request);
    }

    /** Desactiva el tipo (baja lógica). */
    // DELETE /api/catalog/procedures/{id} -> 204 No Content (no borra la fila: solo la marca inactiva).
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('Admin')")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }

    /** Reactiva un tipo desactivado. Idempotente: si ya estaba activo también responde 204. */
    // PUT /api/catalog/procedures/{id}/activate
    @PutMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('Admin')")
    public void activate(@PathVariable Long id) {
        service.activate(id);
    }

    // true si el usuario tiene rol Admin u Operador (personal): esos roles ven también los trámites inactivos.
    private boolean canSeeInactive(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> ADMIN.equals(a) || OPERADOR.equals(a));
    }
}
