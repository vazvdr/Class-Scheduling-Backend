package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.services.ProfessorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/professores")
@Tag(name = "Professores", description = "Todas as operações relacionadas a professores")
public class ProfessorController {

    private final ProfessorService professorService;
    
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @Operation(summary = "Lista todos os professores")
    @GetMapping
    public List<Professor> listarTodos() {
        return professorService.listarTodosProfessores();
    }
    
    @Operation(summary = "Lista os professores por id's")
    @GetMapping(value = "/{id}")
	public ResponseEntity<Professor> findById(@PathVariable Long id) {
		Professor obj = professorService.buscarPorId(id);
		return ResponseEntity.ok().body(obj);
	}

}