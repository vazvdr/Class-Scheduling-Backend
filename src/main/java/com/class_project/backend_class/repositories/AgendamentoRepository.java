package com.class_project.backend_class.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.class_project.backend_class.classes.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
	List<Agendamento> findByDataAndProfessorId(LocalDate data, Long professorId);
	List<Agendamento> findByUsuarioId(Long usuarioId);
	List<Agendamento> findByUsuarioIdAndData(Long usuarioId, LocalDate data);
}