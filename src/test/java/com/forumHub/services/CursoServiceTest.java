package com.forumHub.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.enums.Categoria;
import com.forumHub.domain.repositories.CursoRepository;
import com.forumHub.dtos.curso.CursoRequestDto;
import com.forumHub.dtos.curso.CursoResponseDto;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTest {

    @Mock
    CursoRepository cursoRepository;

    @InjectMocks
    CursoService cursoService;

    Curso curso;
    CursoRequestDto cursoRequestDto;
    CursoResponseDto cursoResponseDto;

    @BeforeEach
    void setUp() {
        curso = Curso.builder().id(1L).nome("Nome curso").categoria(Categoria.BACKEND).build();
    }

    @Test
    @DisplayName("Should find a Curso by name")
    void CursoService_findByName() {
        String name = "Nome curso";

        when(cursoRepository.findByNomeIgnoreCase(name)).thenReturn(Optional.of(curso));

        Optional<Curso> response = cursoService.findbyName(name);

        assertTrue(response.isPresent());
        assertEquals(response.get().getNome(), name);
        assertEquals(response.get(), curso);

    }

    @Test
    @DisplayName("Should not find a Curso by name, when curso doesn't exist")
    void CursoService_findByName_errorCase1() {
        String name = "abc";

        when(cursoRepository.findByNomeIgnoreCase(name)).thenReturn(Optional.empty());

        Optional<Curso> response = cursoService.findbyName(name);

        assertTrue(response.isEmpty());

    }

    @Test
    @DisplayName("Should find a Curso by id")
    void CursoService_findbyId() {
        Long id = 1L;

        when(cursoRepository.findById(id)).thenReturn(Optional.of(curso));

        Optional<Curso> response = cursoService.findbyId(id);

        assertTrue(response.isPresent());
        assertEquals(response.get().getId(), id);
        assertEquals(response.get(), curso);

    }

    @Test
    @DisplayName("Should not find a Curso by id, when curso doesn't exist")
    void CursoService_findbyId_errorCase1() {
        Long id = 8L;

        when(cursoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Curso> response = cursoService.findbyId(id);

        assertTrue(response.isEmpty());

    }

    @Test
    @DisplayName("Should create a Curso, return CursoResponseDto")
    void CursoService_createCurso_returnCursoResponseDto() {

        cursoRequestDto = new CursoRequestDto("Novo curso", Categoria.INFRA);

        curso = Curso.builder().id(2L).nome(cursoRequestDto.nome()).categoria(cursoRequestDto.categoria()).build();

        cursoResponseDto = new CursoResponseDto(curso.getId(), curso.getNome(), curso.getCategoria());

        CursoService cursoServiceSpy = spy(cursoService);

        when(cursoRepository
                .save(Curso.builder().nome(cursoRequestDto.nome()).categoria(cursoRequestDto.categoria()).build()))
                .thenReturn(curso);

        CursoResponseDto response = cursoServiceSpy.createCurso(cursoRequestDto);

        assertEquals(response, cursoResponseDto);

        verify(cursoRepository, times(1))
                .save(Curso.builder().nome(cursoRequestDto.nome()).categoria(cursoRequestDto.categoria()).build());
        verify(cursoServiceSpy, times(1)).toDto(curso);
    }

    @Test
    @DisplayName("Should return a CursoResponseDto")
    void CursoService_toDto_returnCursoResponseDto() {

        cursoResponseDto = new CursoResponseDto(1l, "Nome curso", Categoria.BACKEND);

        assertEquals(cursoResponseDto, cursoService.toDto(curso));

    }

}
