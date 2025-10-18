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
import com.class_project.backend_class.repositories.AssuntoRepository;

@ExtendWith(MockitoExtension.class)
class AssuntoServiceTest {

    @Mock
    private AssuntoRepository assuntoRepository;

    @InjectMocks
    private AssuntoService assuntoService;

    private Assunto assunto;
    private Professor professor;

    @BeforeEach
    void setup() {
        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Pedrinho Backend");

        assunto = new Assunto(1L, "Desenvolvimento Backend", 40, List.of(professor));
    }

    @Test
    void deveListarTodosOsAssuntos() {
        // Arrange
        when(assuntoRepository.findAll()).thenReturn(List.of(assunto));

        // Act
        List<Assunto> resultado = assuntoService.listarTodosOsAssuntos();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Desenvolvimento Backend", resultado.get(0).getNome());
        assertEquals(40, resultado.get(0).getDuracao());
        assertEquals(1, resultado.get(0).getProfessores().size());
        assertEquals("Pedrinho Backend", resultado.get(0).getProfessores().get(0).getNome());

        verify(assuntoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarAssuntoPorIdComSucesso() {
        // Arrange
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));

        // Act
        Assunto resultado = assuntoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Desenvolvimento Backend", resultado.getNome());
        assertEquals(40, resultado.getDuracao());
        assertEquals(1, resultado.getProfessores().size());
        assertEquals("Pedrinho Backend", resultado.getProfessores().get(0).getNome());

        verify(assuntoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoAssuntoNaoEncontrado() {
        // Arrange
        when(assuntoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> assuntoService.buscarPorId(99L));
        verify(assuntoRepository).findById(99L);
    }
}
