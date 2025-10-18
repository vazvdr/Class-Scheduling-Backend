package com.class_project.backend_class.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.classes.Professor;
import com.class_project.backend_class.repositories.ProfessorRepository;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private ProfessorService professorService;

    private Professor professor;
    private Assunto assunto;

    @BeforeEach
    void setup() {
        // Criando exemplo de Assunto
        assunto = new Assunto(1L, "Desenvolvimento Backend", 40, null);

        // Criando exemplo de Professor
        professor = new Professor(1L, "Pedrinho Backend", List.of(assunto));
    }

    @Test
    void deveListarTodosProfessores() {
        // Arrange
        when(professorRepository.findAll()).thenReturn(List.of(professor));

        // Act
        List<Professor> resultado = professorService.listarTodosProfessores();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Pedrinho Backend", resultado.get(0).getNome());
        assertEquals(1, resultado.get(0).getAssuntos().size());
        assertEquals("Desenvolvimento Backend", resultado.get(0).getAssuntos().get(0).getNome());

        verify(professorRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarProfessorPorIdComSucesso() {
        // Arrange
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

        // Act
        Professor resultado = professorService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pedrinho Backend", resultado.getNome());
        assertEquals(1, resultado.getAssuntos().size());
        assertEquals("Desenvolvimento Backend", resultado.getAssuntos().get(0).getNome());

        verify(professorRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoEncontrado() {
        // Arrange
        when(professorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> professorService.buscarPorId(99L));
        verify(professorRepository).findById(99L);
    }
}
