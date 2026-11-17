package cl.barriodigital.catalog.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.catalog.dto.QuotaStatusResponse;
import cl.barriodigital.catalog.service.QuotaService;

/**
 * QUÉ ES: API REST del cupo diario de un tipo de trámite (/api/catalog/procedures/{id}/quota).
 * El @PreAuthorize a nivel de clase aplica a todos los métodos: solo Admin y Operador.
 * Quien llama a "consume" es el microservicio requests cuando se admite una solicitud.
 */
@RestController
@RequestMapping("/api/catalog/procedures/{id}/quota")
@PreAuthorize("hasAnyAuthority('Admin','Operador')")
public class QuotaController {

    private final QuotaService service;

    public QuotaController(QuotaService service) {
        this.service = service;
    }

    /** Cupo configurado, usado y disponible hoy. */
    // GET .../quota
    @GetMapping
    public QuotaStatusResponse status(@PathVariable Long id) {
        return service.status(id);
    }

    /** Descuenta un cupo: 200 si había, 409 si no quedaban. */
    // POST .../quota/consume (el 409 lo produce GlobalExceptionHandler al capturar QuotaExhaustedException)
    @PostMapping("/consume")
    public QuotaStatusResponse consume(@PathVariable Long id) {
        return service.consume(id);
    }
}
