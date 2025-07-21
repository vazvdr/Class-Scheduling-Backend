package com.class_project.backend_class.validations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.class_project.backend_class.classes.Agendamento;
import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.exceptions.AgendamentoException;
import com.class_project.backend_class.repositories.AgendamentoRepository;
import com.class_project.backend_class.repositories.AssuntoRepository;

@Component
public class AgendamentoValidator {
	
	private final AgendamentoRepository agendamentoRepository;
	
	private final AssuntoRepository assuntoRepository;

    public AgendamentoValidator(AssuntoRepository assuntoRepository, AgendamentoRepository agendamentoRepository) {
        this.assuntoRepository = assuntoRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public void validarIntegridadeAssuntoProfessor(Assunto assunto, Professor professor) {
        boolean professorLeciona = assunto.getProfessores().stream()
            .anyMatch(p -> p.getId().equals(professor.getId()));

        if (!professorLeciona) {
            throw new RuntimeException("O professor informado não leciona o assunto selecionado.");
        }
    }

    public void validarSeAulaCabeNoPeriodo(LocalTime inicio, LocalTime fim) {
        boolean cabe = (
            dentroDoPeriodo(inicio, fim, LocalTime.of(8, 0), LocalTime.of(12, 0)) ||   // manhã
            dentroDoPeriodo(inicio, fim, LocalTime.of(14, 0), LocalTime.of(18, 0)) ||  // tarde
            dentroDoPeriodo(inicio, fim, LocalTime.of(19, 0), LocalTime.of(22, 0))     // noite
        );

        if (!cabe) {
            throw new RuntimeException("A aula não cabe em nenhum período permitido (manhã, tarde ou noite).");
        }
    }

    public void validarConflitoDeHorario(LocalTime horarioInicio, LocalTime horarioFim, List<Agendamento> agendamentosNoDia) {
        boolean temConflito = agendamentosNoDia.stream().anyMatch(ag -> {
            LocalTime agInicio = ag.getHorario();
            int duracaoAg = ag.getAssunto().getDuracao();
            LocalTime agFim = agInicio.plusMinutes(duracaoAg);
            return horarioInicio.isBefore(agFim) && horarioFim.isAfter(agInicio);
        });

        if (temConflito) {
            throw new AgendamentoException("Você já tem outra aula nesse horário.");
        }
    }
    
    /**
     * Valida conflitos na agenda do professor, ignorando um agendamento específico.
     * Usado apenas no método de edição.
     */
    public void validarConflitoNaAgendaDoProfessorIgnorandoAgendamento(
        LocalTime novoInicio,
        LocalTime novoFim,
        List<Agendamento> agendamentosDoProfessor,
        Long idParaIgnorar
    ) {
        boolean conflito = agendamentosDoProfessor.stream()
            .filter(ag -> !ag.getId().equals(idParaIgnorar))
            .anyMatch(ag -> {
                LocalTime agInicio = ag.getHorario();
                int duracao = ag.getAssunto().getDuracao();
                LocalTime agFim = agInicio.plusMinutes(duracao);
                return novoInicio.isBefore(agFim) && novoFim.isAfter(agInicio);
            });

        if (conflito) {
            throw new AgendamentoException("O professor já possui outro agendamento nesse horário.");
        }
    }
 // ✅ Validação para edição
    public void validarConflitoAoEditar(Long agendamentoId, Long usuarioId, Long assuntoId, LocalDate data, LocalTime novoInicio) {
        Assunto assunto = assuntoRepository.findById(assuntoId)
                .orElseThrow(() -> new RuntimeException("Assunto não encontrado"));

        LocalTime novoFim = novoInicio.plusMinutes(assunto.getDuracao());

        List<Agendamento> agendamentosNoMesmoDia = agendamentoRepository.findByUsuarioIdAndData(usuarioId, data);

        for (Agendamento agendamento : agendamentosNoMesmoDia) {
            // ⚠️ Ignora o agendamento que está sendo editado
            if (!agendamento.getId().equals(agendamentoId)) {
                LocalTime inicioExistente = agendamento.getHorario();
                LocalTime fimExistente = inicioExistente.plusMinutes(agendamento.getAssunto().getDuracao());

             // Libera agendamentos que começam exatamente no horário de término
                boolean conflito = novoInicio.isBefore(fimExistente) && novoFim.isAfter(inicioExistente);
                
                if (conflito) {
                    throw new RuntimeException("Este horário conflita com outro agendamento seu.");
                }
            }
        }
    }
    private boolean dentroDoPeriodo(LocalTime inicio, LocalTime fim, LocalTime periodoInicio, LocalTime periodoFim) {
        return !inicio.isBefore(periodoInicio) && !fim.isAfter(periodoFim);
    }
}
