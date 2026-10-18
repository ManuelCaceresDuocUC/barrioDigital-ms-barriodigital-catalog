package cl.barriodigital.catalog.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.catalog.dto.QuotaStatusResponse;
import cl.barriodigital.catalog.service.QuotaService;

@RestController
@RequestMapping("/api/catalog/procedures/{id}/quota")
@PreAuthorize("hasAnyAuthority('Admin','Operador')")
public class QuotaController {

    private final QuotaService service;

    public QuotaController(QuotaService service) {
        this.service = service;
    }

    /** Cupo configurado, usado y disponible hoy. */
    @GetMapping
    public QuotaStatusResponse status(@PathVariable Long id) {
        return service.status(id);
    }

    /** Descuenta un cupo: 200 si había, 409 si no quedaban. */
    @PostMapping("/consume")
    public QuotaStatusResponse consume(@PathVariable Long id) {
        return service.consume(id);
    }
}
