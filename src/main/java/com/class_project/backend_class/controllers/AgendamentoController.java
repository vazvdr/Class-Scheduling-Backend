package com.class_project.backend_class.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.class_project.backend_class.dto.agendamento.AgendamentoRequestDTO;
import com.class_project.backend_class.dto.agendamento.AgendamentoResponseDTO;
import com.class_project.backend_class.services.AgendamentoService;
import com.class_project.backend_class.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/agendamentos")
@CrossOrigin(origins = "http://localhost:5173")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@RequestBody AgendamentoRequestDTO dto, HttpServletRequest request) {
        AgendamentoResponseDTO agendamento = agendamentoService.salvar(dto, request);
        return ResponseEntity.ok(agendamento);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarAgendamentosPorUsuario(@PathVariable Long usuarioId) {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.listarAgendamentosPorUsuario(usuarioId);
        return ResponseEntity.ok(agendamentos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> editar(@PathVariable Long id, @RequestBody AgendamentoRequestDTO dto, HttpServletRequest request) {
    	System.out.println("➡️ Recebido PUT agendamento id=" + id);
    	System.out.println("DTO: " + dto.getData() + " " + dto.getHorario());

    	String token = jwtUtil.limparPrefixoBearer(request.getHeader("Authorization"));
        Long usuarioId = jwtUtil.extrairId(token);
        AgendamentoResponseDTO atualizado = agendamentoService.editarAgendamento(id, dto, usuarioId);
        return ResponseEntity.ok(atualizado);
    }

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
