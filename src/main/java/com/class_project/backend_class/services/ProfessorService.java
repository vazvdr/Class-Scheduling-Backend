package com.class_project.backend_class.services;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.repositories.ProfessorRepository;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Cacheable(value = "professores")
    public List<Professor> listarTodosProfessores() {
		System.out.println("Buscando professores no banco de dados");
        return professorRepository.findAll();
    }
    
    @Cacheable(value = "professor", key = "#id")
    public Professor buscarPorId(Long id) {
        Optional<Professor> obj = professorRepository.findById(id);
        return obj.orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }
}
