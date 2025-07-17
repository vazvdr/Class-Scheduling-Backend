package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.services.AssuntoService;

@RestController
@RequestMapping("/assuntos")
public class AssuntoController {

	private final AssuntoService assuntoService;

    public AssuntoController(AssuntoService assuntoService) {
        this.assuntoService = assuntoService;
    }
	
	@GetMapping
	public List<Assunto> listarTodos(){
		return assuntoService.listarTodosOsAssuntos();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Assunto> findById(@PathVariable Long id){
		Assunto obj = assuntoService.buscarPorId(id);
		return ResponseEntity.ok().body(obj);
		
	}
	
}