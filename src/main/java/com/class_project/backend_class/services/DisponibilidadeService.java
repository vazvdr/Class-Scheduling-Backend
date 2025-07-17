package com.class_project.backend_class.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Agendamento;
import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.repositories.AgendamentoRepository;
import com.class_project.backend_class.repositories.AssuntoRepository;

@Service
public class DisponibilidadeService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AssuntoRepository assuntoRepository;

    private final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public List<LocalDate> gerarProximosDiasUteis(int dias) {
        List<LocalDate> diasUteis = new ArrayList<>();
        LocalDate dataAtual = LocalDate.now();

        while (diasUteis.size() < dias) {
            if (!(dataAtual.getDayOfWeek() == DayOfWeek.SATURDAY || dataAtual.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                diasUteis.add(dataAtual);
            }
            dataAtual = dataAtual.plusDays(1);
        }

        return diasUteis;
    }

    public Map<String, List<String>> gerarHorariosPorPeriodo(LocalDate data, Long professorId, Long assuntoId) {
        Optional<Assunto> assuntoOpt = assuntoRepository.findById(assuntoId);
        if (assuntoOpt.isEmpty()) {
            throw new RuntimeException("Assunto não encontrado");
        }

        Assunto assunto = assuntoOpt.get();

        boolean professorLeciona = assunto.getProfessores().stream()
            .anyMatch(prof -> prof.getId().equals(professorId));

        if (!professorLeciona) {
            throw new RuntimeException("O professor informado não leciona o assunto selecionado.");
        }

        int duracaoAssuntoSelecionado = assunto.getDuracao();

        Map<String, List<String>> periodos = new LinkedHashMap<>();
        periodos.put("manha", gerarHorariosNoIntervalo(data, professorId, LocalTime.of(8, 0), LocalTime.of(12, 0), duracaoAssuntoSelecionado));
        periodos.put("tarde", gerarHorariosNoIntervalo(data, professorId, LocalTime.of(14, 0), LocalTime.of(18, 0), duracaoAssuntoSelecionado));
        periodos.put("noite", gerarHorariosNoIntervalo(data, professorId, LocalTime.of(19, 0), LocalTime.of(22, 0), duracaoAssuntoSelecionado));

        return periodos;
    }

    private List<String> gerarHorariosNoIntervalo(LocalDate data, Long professorId, LocalTime inicio, LocalTime fim, int duracaoAssuntoSelecionado) {
        List<String> horarios = new ArrayList<>();
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAndProfessorId(data, professorId);

        while (!inicio.isAfter(fim.minusMinutes(15))) {
            LocalTime horarioTentado = inicio;
            
            boolean conflita = agendamentos.stream().anyMatch(agendamento -> {
                LocalTime agInicio = agendamento.getHorario();
                int agDuracao = agendamento.getAssunto().getDuracao();
                LocalTime agFim = agInicio.plusMinutes(agDuracao);

                return !(horarioTentado.isBefore(agInicio) || horarioTentado.isAfter(agFim.minusMinutes(15)));
            });

            String horarioFormatado = horarioTentado.format(horaFormatter);
            if (conflita) {
                horarios.add(horarioFormatado + " (ocupado)");
            } else {
                horarios.add(horarioFormatado);
            }

            inicio = inicio.plusMinutes(15);
        }

        return horarios;
    }
    
    public Map<String, List<String>> gerarHorariosPorPeriodoIgnorandoAgendamento(LocalDate data, Long professorId, Long assuntoId, Long agendamentoId) {
        Optional<Assunto> assuntoOpt = assuntoRepository.findById(assuntoId);
        if (assuntoOpt.isEmpty()) {
            throw new RuntimeException("Assunto não encontrado");
        }

        Assunto assunto = assuntoOpt.get();

        boolean professorLeciona = assunto.getProfessores().stream()
            .anyMatch(prof -> prof.getId().equals(professorId));

        if (!professorLeciona) {
            throw new RuntimeException("O professor informado não leciona o assunto selecionado.");
        }

        int duracaoAssuntoSelecionado = assunto.getDuracao();

        Map<String, List<String>> periodos = new LinkedHashMap<>();
        periodos.put("manha", gerarHorariosNoIntervaloIgnorandoAgendamento(data, professorId, LocalTime.of(8, 0), LocalTime.of(12, 0), duracaoAssuntoSelecionado, agendamentoId));
        periodos.put("tarde", gerarHorariosNoIntervaloIgnorandoAgendamento(data, professorId, LocalTime.of(14, 0), LocalTime.of(18, 0), duracaoAssuntoSelecionado, agendamentoId));
        periodos.put("noite", gerarHorariosNoIntervaloIgnorandoAgendamento(data, professorId, LocalTime.of(19, 0), LocalTime.of(22, 0), duracaoAssuntoSelecionado, agendamentoId));

        return periodos;
    }

    private List<String> gerarHorariosNoIntervaloIgnorandoAgendamento(LocalDate data, Long professorId, LocalTime inicio, LocalTime fim, int duracaoAssuntoSelecionado, Long agendamentoId) {
        List<String> horarios = new ArrayList<>();
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAndProfessorId(data, professorId)
            .stream()
            .filter(a -> !a.getId().equals(agendamentoId)) // ignora o agendamento atual
            .toList();

        while (!inicio.isAfter(fim.minusMinutes(15))) {
            LocalTime horarioTentado = inicio;

            boolean conflita = agendamentos.stream().anyMatch(agendamento -> {
                LocalTime agInicio = agendamento.getHorario();
                int agDuracao = agendamento.getAssunto().getDuracao();
                LocalTime agFim = agInicio.plusMinutes(agDuracao);

                return !(horarioTentado.isBefore(agInicio) || horarioTentado.isAfter(agFim.minusMinutes(15)));
            });

            String horarioFormatado = horarioTentado.format(horaFormatter);
            if (conflita) {
                horarios.add(horarioFormatado + " (ocupado)");
            } else {
                horarios.add(horarioFormatado);
            }

            inicio = inicio.plusMinutes(15);
        }

        return horarios;
    }


}
