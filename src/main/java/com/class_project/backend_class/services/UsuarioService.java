package com.class_project.backend_class.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.usuario.UsuarioRequestDTO;
import com.class_project.backend_class.dto.usuario.UsuarioResponseDTO;
import com.class_project.backend_class.exceptions.UsuarioException;
import com.class_project.backend_class.mappers.UsuarioMapper;
import com.class_project.backend_class.repositories.UsuarioRepository;
import com.class_project.backend_class.utils.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UsuarioException("E-mail já está em uso.");
        }

        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(salvo);
    }

    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new UsuarioException("Senha incorreta");
        }

        return jwtUtil.gerarToken(usuario);
    }

    public UsuarioResponseDTO editarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioException("Usuário não encontrado"));

        UsuarioMapper.updateEntityFromDTO(dto, usuario);

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional
    public void deletarUsuario(Long id) {
        agendamentoService.deletarTodosAgendamentosPorUsuario(id);
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsuarioException("Usuário não encontrado"));

        return UsuarioMapper.toResponseDTO(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha(),
                List.of()
        );
    }

    public void processarRecuperacaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOpt.get();
        String token = jwtUtil.gerarTokenResetSenha(usuario);

        String link = "http://localhost:5173/resetar-senha?token=" + token;

        String assunto = "Recuperação de senha - Class Scheduling";
        String corpo = "Olá, " + usuario.getNome() + ",\n\n"
                + "Clique no link abaixo para redefinir sua senha. Ele expira em 30 minutos:\n" + link;

        emailService.enviarEmailRecuperarSenha(usuario.getEmail(), assunto, corpo);
    }
}
