package com.forumHub.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.repositories.CursoRepository;
import com.forumHub.dtos.curso.CursoRequestDto;
import com.forumHub.dtos.curso.CursoResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public Optional<Curso> findbyName(String name) {
        return cursoRepository.findByNomeIgnoreCase(name);
    }

    public Optional<Curso> findbyId(Long id) {
        return cursoRepository.findById(id);
    }

    public Curso createCurso(CursoRequestDto data) {
        Curso curso = Curso.builder()
                .nome(data.nome())
                .categoria(data.categoria())
                .build();

        return cursoRepository.save(curso);
    }

    public CursoResponseDto toDto(Curso curso) {
        return new CursoResponseDto(curso.getId(), curso.getNome(), curso.getCategoria());
    }

}
