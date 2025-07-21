package com.class_project.backend_class.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.services.DisponibilidadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/disponibilidade")
@Tag(name = "Disponibilidade", description = "Endpoint que mostra as datas e horarios disponíveis para agendamento")
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeService disponibilidadeService;

    @Operation(summary = "Lista as datas e horários disponíveis")
    @GetMapping
    public ResponseEntity<?> listarDisponibilidade(
        @RequestParam Long professorId,
        @RequestParam Long assuntoId
    ) {
        List<LocalDate> dias = disponibilidadeService.gerarProximosDiasUteis(7);

        Map<String, Map<String, List<String>>> resposta = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate dia : dias) {
            Map<String, List<String>> horariosPorPeriodo = disponibilidadeService.gerarHorariosPorPeriodo(dia, professorId, assuntoId);

            boolean possuiHorarios = horariosPorPeriodo.values().stream().anyMatch(list -> !list.isEmpty());
            if (possuiHorarios) {
                resposta.put(dia.format(formatter), horariosPorPeriodo);
            }
        }

        return ResponseEntity.ok(resposta);
    }
    
    @Operation(summary = "Lista as datas e horários disponíveis não marcando como ocupado o agendamento a ser editado")
    @GetMapping("/editar")
    public ResponseEntity<?> listarDisponibilidadeParaEdicao(
        @RequestParam Long professorId,
        @RequestParam Long assuntoId,
        @RequestParam Long agendamentoId
    ) {
        List<LocalDate> dias = disponibilidadeService.gerarProximosDiasUteis(7);

        Map<String, Map<String, List<String>>> resposta = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate dia : dias) {
            Map<String, List<String>> horariosPorPeriodo = disponibilidadeService.gerarHorariosPorPeriodoIgnorandoAgendamento(
                dia, professorId, assuntoId, agendamentoId
            );

            boolean possuiHorarios = horariosPorPeriodo.values().stream().anyMatch(list -> !list.isEmpty());
            if (possuiHorarios) {
                resposta.put(dia.format(formatter), horariosPorPeriodo);
            }
        }

        return ResponseEntity.ok(resposta);
    }



}
