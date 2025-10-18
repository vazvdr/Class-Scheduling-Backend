package com.class_project.backend_class.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.agendamento.AgendamentoRequestDTO;
import com.class_project.backend_class.dto.agendamento.AgendamentoResponseDTO;
import com.class_project.backend_class.repositories.AgendamentoRepository;
import com.class_project.backend_class.repositories.AssuntoRepository;
import com.class_project.backend_class.repositories.ProfessorRepository;
import com.class_project.backend_class.repositories.UsuarioRepository;
import com.class_project.backend_class.utils.JwtUtil;
import com.class_project.backend_class.validations.AgendamentoValidator;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private AssuntoRepository assuntoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private AgendamentoValidator agendamentoValidator;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario;
    private Professor professor;
    private Assunto assunto;
    private Agendamento agendamento;
    private AgendamentoRequestDTO agendamentoDTO;

    @BeforeEach
    void setup() {
        usuario = new Usuario(1L, "Vanderson", "vanderson@test.com", "123456", "999999999");
        professor = new Professor(1L, "Pedrinho Backend", null);
        assunto = new Assunto(1L, "Desenvolvimento Backend", 60, List.of(professor));
        agendamento = new Agendamento(1L, usuario, assunto, professor, LocalDate.now(), LocalTime.of(10, 0));

        agendamentoDTO = new AgendamentoRequestDTO();
        agendamentoDTO.setAssuntoId(1L);
        agendamentoDTO.setProfessorId(1L);
        agendamentoDTO.setData(LocalDate.now());
        agendamentoDTO.setHorario(LocalTime.of(10, 0));
    }

    // ===== TESTES SALVAR AGENDAMENTO =====

    @Test
    void deveLancarExcecaoQuandoTokenAusente() {
        when(request.getHeader("Authorization")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.salvar(agendamentoDTO, request)
        );

        assertEquals("Token JWT não fornecido ou inválido", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extrairId("token123")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.salvar(agendamentoDTO, request)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoAssuntoNaoEncontrado() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extrairId("token123")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(assuntoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.salvar(agendamentoDTO, request)
        );

        assertEquals("Assunto não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoEncontrado() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extrairId("token123")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.salvar(agendamentoDTO, request)
        );

        assertEquals("Professor não encontrado", ex.getMessage());
    }

    @Test
    void deveSalvarAgendamentoComSucesso() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(jwtUtil.extrairId("token123")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(agendamentoRepository.findByDataAndProfessorId(any(LocalDate.class), eq(1L))).thenReturn(List.of());
        when(agendamentoRepository.findByUsuarioIdAndData(1L, LocalDate.now())).thenReturn(List.of());
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoResponseDTO response = agendamentoService.salvar(agendamentoDTO, request);

        assertNotNull(response);
        assertEquals("Desenvolvimento Backend", response.getNomeAssunto());
        verify(emailService, times(1)).enviarEmailAgendamento(anyString(), anyString(), anyString());
    }

    // ===== TESTES EDITAR AGENDAMENTO =====

    @Test
    void deveEditarAgendamentoComSucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(agendamentoRepository.findByDataAndProfessorId(any(LocalDate.class), eq(1L))).thenReturn(List.of());
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);

        AgendamentoResponseDTO response = agendamentoService.editarAgendamento(1L, agendamentoDTO, 1L);

        assertNotNull(response);
        assertEquals(LocalTime.of(10, 0), response.getHorario());
    }

    @Test
    void deveLancarExcecaoAoEditarAgendamentoDeOutroUsuario() {
        Usuario outroUsuario = new Usuario(2L, "Outro", "outro@test.com", "123", "999");
        agendamento.setUsuario(outroUsuario);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.editarAgendamento(1L, agendamentoDTO, 1L)
        );

        assertEquals("Você não tem permissão para editar este agendamento", ex.getMessage());
    }

    // ===== TESTES DELETAR AGENDAMENTO =====

    @Test
    void deveDeletarAgendamentoComSucesso() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        doNothing().when(agendamentoRepository).deleteById(1L);

        assertDoesNotThrow(() -> agendamentoService.deletarAgendamento(1L, 1L));
        verify(agendamentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarAgendamentoDeOutroUsuario() {
        Usuario outroUsuario = new Usuario(2L, "Outro", "outro@test.com", "123", "999");
        agendamento.setUsuario(outroUsuario);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            agendamentoService.deletarAgendamento(1L, 1L)
        );

        assertEquals("Você não tem permissão para deletar este agendamento", ex.getMessage());
    }

    @Test
    void deveDeletarTodosAgendamentosDoUsuario() {
        when(agendamentoRepository.findByUsuarioId(1L)).thenReturn(List.of(agendamento));
        doNothing().when(agendamentoRepository).deleteAll(List.of(agendamento));

        assertDoesNotThrow(() -> agendamentoService.deletarTodosAgendamentosPorUsuario(1L));
        verify(agendamentoRepository, times(1)).deleteAll(List.of(agendamento));
    }
}
