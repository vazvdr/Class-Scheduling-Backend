package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.services.ProfessorService;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;
    
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public List<Professor> listarTodos() {
        return professorService.listarTodosProfessores();
    }
    
    @GetMapping(value = "/{id}")
	public ResponseEntity<Professor> findById(@PathVariable Long id) {
		Professor obj = professorService.buscarPorId(id);
		return ResponseEntity.ok().body(obj);
	}

}