package com.class_project.backend_class.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.repositories.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<Professor> listarTodosProfessores() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
    	Optional<Professor> obj = professorRepository.findById(id);
        return obj.get();
    }
}
