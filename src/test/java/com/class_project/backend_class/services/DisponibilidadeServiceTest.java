package com.class_project.backend_class.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.class_project.backend_class.classes.Agendamento;
import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.repositories.AgendamentoRepository;
import com.class_project.backend_class.repositories.AssuntoRepository;

@ExtendWith(MockitoExtension.class)
class DisponibilidadeServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private AssuntoRepository assuntoRepository;

    @InjectMocks
    private DisponibilidadeService disponibilidadeService;

    private Professor professor;
    private Assunto assunto;
    private Agendamento agendamento;

    @BeforeEach
    void setup() {
        professor = new Professor(1L, "Pedrinho Backend", null);
        assunto = new Assunto(1L, "Desenvolvimento Backend", 60, List.of(professor));
        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setAssunto(assunto);
        agendamento.setProfessor(professor);
        agendamento.setData(LocalDate.now());
        agendamento.setHorario(LocalTime.of(9, 0));
    }

    @Test
    void deveGerarProximosDiasUteis() {
        List<LocalDate> diasUteis = disponibilidadeService.gerarProximosDiasUteis(5);
        assertEquals(5, diasUteis.size());
        diasUteis.forEach(dia -> {
            assertFalse(dia.getDayOfWeek().getValue() == 6 || dia.getDayOfWeek().getValue() == 7); // não é sábado ou domingo
        });
    }

    @Test
    void deveGerarHorariosPorPeriodo() {
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(agendamentoRepository.findByDataAndProfessorId(any(LocalDate.class), eq(1L)))
            .thenReturn(List.of(agendamento));

        Map<String, List<String>> periodos = disponibilidadeService.gerarHorariosPorPeriodo(LocalDate.now(), 1L, 1L);

        assertNotNull(periodos);
        assertTrue(periodos.containsKey("manha"));
        assertTrue(periodos.get("manha").stream().anyMatch(h -> h.contains("ocupado"))); // 9:00 deve estar ocupado
        assertTrue(periodos.get("tarde").stream().allMatch(h -> !h.contains("ocupado"))); // tarde não tem agendamento
    }

    @Test
    void deveLancarExcecaoSeProfessorNaoLecionaAssunto() {
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            disponibilidadeService.gerarHorariosPorPeriodo(LocalDate.now(), 2L, 1L)
        );
        assertEquals("O professor informado não leciona o assunto selecionado.", exception.getMessage());
    }

    @Test
    void deveGerarHorariosIgnorandoAgendamentoEspecifico() {
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(agendamentoRepository.findByDataAndProfessorId(any(LocalDate.class), eq(1L)))
            .thenReturn(List.of(agendamento));

        Map<String, List<String>> periodos = disponibilidadeService.gerarHorariosPorPeriodoIgnorandoAgendamento(LocalDate.now(), 1L, 1L, 1L);

        assertNotNull(periodos);
        assertTrue(periodos.get("manha").stream().allMatch(h -> !h.contains("ocupado"))); // o agendamento 1 deve ser ignorado
    }
}
