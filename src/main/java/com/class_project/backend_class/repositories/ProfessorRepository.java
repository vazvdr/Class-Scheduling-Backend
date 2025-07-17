package com.class_project.backend_class.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.class_project.backend_class.classes.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long>{

}
