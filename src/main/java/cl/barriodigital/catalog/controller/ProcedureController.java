package cl.barriodigital.catalog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.catalog.model.ProcedureType;
import cl.barriodigital.catalog.service.ProcedureService;

@RestController
@RequestMapping("/api/catalog/procedures")
public class ProcedureController {

    @Autowired
    private ProcedureService service;

    // GET /api/catalog/procedures
    @GetMapping
    public ResponseEntity<List<ProcedureType>> getAll() {
        return ResponseEntity.ok(service.getAllProcedures());
    }

    // POST /api/catalog/procedures
    @PostMapping
    public ResponseEntity<ProcedureType> create(@RequestBody ProcedureType procedure) {
        return new ResponseEntity<>(service.createProcedure(procedure), HttpStatus.CREATED);
    }

    // PUT /api/catalog/procedures/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProcedureType> update(@PathVariable Long id, @RequestBody ProcedureType procedure) {
        try {
            return ResponseEntity.ok(service.updateProcedure(id, procedure));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 si el ID no existe
        }
    }
}