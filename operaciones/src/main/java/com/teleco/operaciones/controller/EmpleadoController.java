package com.teleco.operaciones.controller;

import com.teleco.operaciones.model.empleados.EmpleadoRequest;
import com.teleco.operaciones.model.empleados.Empleado;
import com.teleco.operaciones.service.empleados.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost:3000")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<Empleado> listar() {
        return empleadoService.listar();
    }

    @GetMapping("/{id}")
    public Empleado buscarPorId(@PathVariable UUID id) {
        return empleadoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado crear(@RequestBody EmpleadoRequest request) {
        return empleadoService.crear(request);
    }

    @PutMapping("/{id}")
    public Empleado actualizar(
            @PathVariable UUID id,
            @RequestBody EmpleadoRequest request
    ) {
        return empleadoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        empleadoService.eliminar(id);
    }
}