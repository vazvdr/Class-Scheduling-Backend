package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.services.AssuntoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/assuntos")
@Tag(name = "Assuntos", description = "Operações relacionadas aos assuntos das aulas")
public class AssuntoController {

	private final AssuntoService assuntoService;

    public AssuntoController(AssuntoService assuntoService) {
        this.assuntoService = assuntoService;
    }
	
    @Operation(summary = "Lista todos os assuntos ministrados na escola")
	@GetMapping
	public List<Assunto> listarTodos(){
		return assuntoService.listarTodosOsAssuntos();
	}
	
    @Operation(summary = "Lista por id's os assuntos ministrados na escola")
	@GetMapping("/{id}")
	public ResponseEntity<Assunto> findById(@PathVariable Long id){
		Assunto obj = assuntoService.buscarPorId(id);
		return ResponseEntity.ok().body(obj);
		
	}
	
}