package com.class_project.backend_class.mappers;

import com.class_project.backend_class.classes.Agendamento;
import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.agendamento.AgendamentoRequestDTO;
import com.class_project.backend_class.dto.agendamento.AgendamentoResponseDTO;

public class AgendamentoMapper {

    public static Agendamento toEntity(AgendamentoRequestDTO dto, Usuario usuario, Professor professor, Assunto assunto) {
        Agendamento agendamento = new Agendamento();
        agendamento.setUsuario(usuario);
        agendamento.setProfessor(professor);
        agendamento.setAssunto(assunto);
        agendamento.setData(dto.getData());
        agendamento.setHorario(dto.getHorario());
        return agendamento;
    }

    public static AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getData(),
                agendamento.getHorario(),
                agendamento.getProfessor().getNome(),
                agendamento.getAssunto().getNome()
        );
    }
}
