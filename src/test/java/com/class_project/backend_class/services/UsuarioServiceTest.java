package com.class_project.backend_class.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.class_project.backend_class.classes.Usuario;
import com.class_project.backend_class.dto.usuario.UsuarioRequestDTO;
import com.class_project.backend_class.dto.usuario.UsuarioResponseDTO;
import com.class_project.backend_class.exceptions.UsuarioException;
import com.class_project.backend_class.repositories.UsuarioRepository;
import com.class_project.backend_class.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Vanderson");
        usuario.setEmail("vanderson@example.com");
        usuario.setSenha(new BCryptPasswordEncoder().encode("123456"));
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
    	UsuarioRequestDTO dto = new UsuarioRequestDTO();
    	dto.setNome("Vanderson");
    	dto.setEmail("vanderson@example.com");
    	dto.setSenha("123456");
    	dto.setTelefone("999999999");

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        assertNotNull(response);
        assertEquals(dto.getEmail(), response.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEmailExistente() {
    	UsuarioRequestDTO dto = new UsuarioRequestDTO();
    	dto.setNome("Vanderson");
    	dto.setEmail("vanderson@example.com");
    	dto.setSenha("123456");
    	dto.setTelefone("999999999");


        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(UsuarioException.class, () -> usuarioService.cadastrar(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveFazerLoginComSucesso() {
        when(usuarioRepository.findByEmail("vanderson@example.com")).thenReturn(Optional.of(usuario));
        when(jwtUtil.gerarToken(usuario)).thenReturn("fake-jwt-token");

        String token = usuarioService.login("vanderson@example.com", "123456");

        assertEquals("fake-jwt-token", token);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExisteNoLogin() {
        when(usuarioRepository.findByEmail("inexistente@example.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioException.class, () -> usuarioService.login("inexistente@example.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorretaNoLogin() {
        when(usuarioRepository.findByEmail("vanderson@example.com")).thenReturn(Optional.of(usuario));

        assertThrows(UsuarioException.class, () -> usuarioService.login("vanderson@example.com", "senhaerrada"));
    }

    @Test
    void deveDeletarUsuarioComSucesso() {
        doNothing().when(agendamentoService).deletarTodosAgendamentosPorUsuario(1L);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.deletarUsuario(1L);

        verify(agendamentoService).deletarTodosAgendamentosPorUsuario(1L);
        verify(usuarioRepository).deleteById(1L);
    }
}
