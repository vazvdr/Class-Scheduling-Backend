package com.class_project.backend_class.dto.agendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoResponseDTO {

    private Long id;
    private LocalDate data;
    private LocalTime horario;
    private String nomeProfessor;
    private String nomeAssunto;

    public AgendamentoResponseDTO(Long id, LocalDate data, LocalTime horario,
                                   String nomeProfessor, String nomeAssunto) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.nomeProfessor = nomeProfessor;
        this.nomeAssunto = nomeAssunto;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getNomeProfessor() {
		return nomeProfessor;
	}

	public void setNomeProfessor(String nomeProfessor) {
		this.nomeProfessor = nomeProfessor;
	}

	public String getNomeAssunto() {
		return nomeAssunto;
	}

	public void setNomeAssunto(String nomeAssunto) {
		this.nomeAssunto = nomeAssunto;
	}

}