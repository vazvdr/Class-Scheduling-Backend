package com.class_project.backend_class.controllers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.class_project.backend_class.classes.PasswordResetToken;
import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.usuario.LoginRequestDTO;
import com.class_project.backend_class.dto.usuario.RecuperarSenhaDTO;
import com.class_project.backend_class.dto.usuario.RedefinirSenhaDTO;
import com.class_project.backend_class.dto.usuario.UsuarioRequestDTO;
import com.class_project.backend_class.dto.usuario.UsuarioResponseDTO;
import com.class_project.backend_class.repositories.PasswordResetTokenRepository;
import com.class_project.backend_class.repositories.UsuarioRepository;
import com.class_project.backend_class.services.EmailService;
import com.class_project.backend_class.services.UsuarioService;
import com.class_project.backend_class.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.cadastrar(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginDTO) {
        try {
            String token = usuarioService.login(loginDTO.getEmail(), loginDTO.getSenha());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editarUsuario(@RequestBody @Valid UsuarioRequestDTO dto, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token não fornecido"));
            }

            token = token.substring(7);
            Long usuarioId = jwtUtil.extrairId(token);

            UsuarioResponseDTO response = usuarioService.editarUsuario(usuarioId, dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/deletar")
    public ResponseEntity<?> deletarUsuario(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Token não fornecido"));
            }

            token = token.substring(7);
            Long usuarioId = jwtUtil.extrairId(token);
            usuarioService.deletarUsuario(usuarioId);

            return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Erro ao deletar usuário"));
        }
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@Valid @RequestBody RecuperarSenhaDTO dto) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(dto.getEmail());

        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Esse email não está cadastrado.");
        }

        Usuario usuario = optionalUsuario.get();

        String token = jwtUtil.gerarTokenResetSenha(usuario);
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setExpiration(expiration);

        tokenRepository.save(resetToken);

        String link = "http://localhost:5173/redefinir-senha?token=" + token;
        emailService.enviarEmailRecuperarSenha(
                usuario.getEmail(),
                "Redefinição de senha",
                "Clique no link para redefinir sua senha: " + link
        );

        System.out.println("✅ Email enviado com sucesso para: " + usuario.getEmail());
        return ResponseEntity.ok("Email enviado com sucesso.");
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaDTO dto) {
        Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(dto.getToken());

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token inválido");
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(new BCryptPasswordEncoder().encode(dto.getNovaSenha()));
        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }
}
