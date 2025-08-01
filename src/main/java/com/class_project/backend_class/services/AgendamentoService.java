package com.class_project.backend_class.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Agendamento;
import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.agendamento.AgendamentoRequestDTO;
import com.class_project.backend_class.dto.agendamento.AgendamentoResponseDTO;
import com.class_project.backend_class.mappers.AgendamentoMapper;
import com.class_project.backend_class.repositories.AgendamentoRepository;
import com.class_project.backend_class.repositories.AssuntoRepository;
import com.class_project.backend_class.repositories.ProfessorRepository;
import com.class_project.backend_class.repositories.UsuarioRepository;
import com.class_project.backend_class.utils.JwtUtil;
import com.class_project.backend_class.validations.AgendamentoValidator;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AssuntoRepository assuntoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AgendamentoValidator agendamentoValidator;

    public AgendamentoResponseDTO salvar(AgendamentoRequestDTO dto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT não fornecido ou inválido");
        }

        String token = authHeader.replace("Bearer ", "");
        Long usuarioId = jwtUtil.extrairId(token);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Assunto assunto = assuntoRepository.findById(dto.getAssuntoId())
                .orElseThrow(() -> new RuntimeException("Assunto não encontrado"));

        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        LocalDate data = dto.getData();
        LocalTime horarioInicio = dto.getHorario();
        int duracao = assunto.getDuracao();
        LocalTime horarioFim = horarioInicio.plusMinutes(duracao);

        agendamentoValidator.validarIntegridadeAssuntoProfessor(assunto, professor);
        agendamentoValidator.validarSeAulaCabeNoPeriodo(horarioInicio, horarioFim);

        List<Agendamento> agendamentosDoProfessorNoDia = agendamentoRepository.findByDataAndProfessorId(data, professor.getId());
        agendamentoValidator.validarConflitoDeHorario(horarioInicio, horarioFim, agendamentosDoProfessorNoDia);

        List<Agendamento> agendamentosDoUsuarioNoDia = agendamentoRepository.findByUsuarioIdAndData(usuarioId, data);
        agendamentoValidator.validarConflitoDeHorario(horarioInicio, horarioFim, agendamentosDoUsuarioNoDia);

        Agendamento agendamento = AgendamentoMapper.toEntity(dto, usuario, professor, assunto);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        String assuntoEmail = "Confirmação de Agendamento";
        String corpoEmail = "Olá " + usuario.getNome() + ",\n\n"
                + "Seu agendamento foi confirmado com sucesso!\n\n"
                + "📚 Assunto: " + assunto.getNome() + "\n"
                + "👨‍🏫 Professor: " + professor.getNome() + "\n"
                + "📅 Data: " + data + "\n"
                + "🕒 Horário: " + horarioInicio + "\n\n"
                + "Obrigado por usar o Class Scheduling!";

        try {
            emailService.enviarEmailAgendamento(usuario.getEmail(), assuntoEmail, corpoEmail);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
            e.printStackTrace();
        }

        return AgendamentoMapper.toResponseDTO(agendamentoSalvo);
    }

    public List<AgendamentoResponseDTO> listarAgendamentosPorUsuario(Long usuarioId) {
        List<LocalDate> diasUteis = new ArrayList<>();

        // ✅ Obtem a data atual com fuso horário brasileiro
        ZoneId zonaBrasil = ZoneId.of("America/Sao_Paulo");
        LocalDate data = ZonedDateTime.now(zonaBrasil).toLocalDate();

        int adicionados = 0;

        while (adicionados < 6) {
            DayOfWeek dia = data.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                diasUteis.add(data);
                adicionados++;
            }
            data = data.plusDays(1);
        }

        return agendamentoRepository.findByUsuarioId(usuarioId).stream()
            .filter(agendamento ->
                diasUteis.stream().anyMatch(dia -> dia.isEqual(agendamento.getData()))
            )
            .map(AgendamentoMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    public AgendamentoResponseDTO editarAgendamento(Long id, AgendamentoRequestDTO dto, Long usuarioId) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!existente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Você não tem permissão para editar este agendamento");
        }

        Assunto novoAssunto = assuntoRepository.findById(dto.getAssuntoId())
                .orElseThrow(() -> new RuntimeException("Assunto não encontrado"));
        Professor professor = professorRepository.findById(dto.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        LocalDate data = dto.getData();
        LocalTime novoInicio = dto.getHorario();

        // ✅ Validação que ignora o próprio agendamento internamente
        agendamentoValidator.validarConflitoAoEditar(id, usuarioId, dto.getAssuntoId(), data, novoInicio);

        // ✅ Valida conflitos com agenda do professor (também ignora o próprio agendamento)
        int novaDuracao = novoAssunto.getDuracao();
        LocalTime novoFim = novoInicio.plusMinutes(novaDuracao);
        List<Agendamento> agendamentosDoProfessorNoDia = agendamentoRepository.findByDataAndProfessorId(data, professor.getId());
        agendamentoValidator.validarConflitoNaAgendaDoProfessorIgnorandoAgendamento(
            novoInicio, novoFim, agendamentosDoProfessorNoDia, id
        );

        // ✅ Atualiza os dados
        existente.setAssunto(novoAssunto);
        existente.setProfessor(professor);
        existente.setData(data);
        existente.setHorario(novoInicio);

        Agendamento atualizado = agendamentoRepository.save(existente);
        return AgendamentoMapper.toResponseDTO(atualizado);
    }
    
    public void deletarAgendamento(Long id, Long usuarioId) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!existente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Você não tem permissão para deletar este agendamento");
        }

        agendamentoRepository.deleteById(id);
    }

    public void deletarTodosAgendamentosPorUsuario(Long usuarioId) {
        List<Agendamento> agendamentos = agendamentoRepository.findByUsuarioId(usuarioId);
        agendamentoRepository.deleteAll(agendamentos);
    }
}
