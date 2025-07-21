package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.dto.agendamento.AgendamentoRequestDTO;
import com.class_project.backend_class.dto.agendamento.AgendamentoResponseDTO;
import com.class_project.backend_class.services.AgendamentoService;
import com.class_project.backend_class.utils.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/agendamentos")
@Tag(name = "Agendamentos", description = "Todas as operações crud relacionadas a agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Cria um novo agendamento")
    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@RequestBody AgendamentoRequestDTO dto, HttpServletRequest request) {
        AgendamentoResponseDTO agendamento = agendamentoService.salvar(dto, request);
        return ResponseEntity.ok(agendamento);
    }

    @Operation(summary = "Lista os agendamentos passando o id do usuário como parâmetro")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentosPorUsuario(@PathVariable Long usuarioId) {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.listarAgendamentosPorUsuario(usuarioId);
        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Altera um agendamento passando o id do mesmo como parâmetro")
    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> editar(@PathVariable Long id, @RequestBody AgendamentoRequestDTO dto, HttpServletRequest request) {
    	System.out.println("➡️ Recebido PUT agendamento id=" + id);
    	System.out.println("DTO: " + dto.getData() + " " + dto.getHorario());

    	String token = jwtUtil.limparPrefixoBearer(request.getHeader("Authorization"));
        Long usuarioId = jwtUtil.extrairId(token);
        AgendamentoResponseDTO atualizado = agendamentoService.editarAgendamento(id, dto, usuarioId);
        return ResponseEntity.ok(atualizado);
    }
    
    @Operation(summary = "Deleta um agendamento passando o id do mesmo como parâmetro")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long usuarioId = jwtUtil.extrairId(token);
        agendamentoService.deletarAgendamento(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
    
}
