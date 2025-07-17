package com.class_project.backend_class.dto.agendamento;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoRequestDTO {

    @NotNull(message = "O ID do professor é obrigatório")
    private Long professorId;

    @NotNull(message = "O ID do assunto é obrigatório")
    private Long assuntoId;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotNull(message = "O horário é obrigatório")
    private LocalTime horario;

	public Long getProfessorId() {
		return professorId;
	}

	public void setProfessorId(Long professorId) {
		this.professorId = professorId;
	}

	public Long getAssuntoId() {
		return assuntoId;
	}

	public void setAssuntoId(Long assuntoId) {
		this.assuntoId = assuntoId;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}

}