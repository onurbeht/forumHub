package com.forumHub.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.forumHub.domain.entities.Curso;
import com.forumHub.dtos.curso.CursoRequestDto;
import com.forumHub.dtos.curso.CursoResponseDto;
import com.forumHub.services.CursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @PostMapping("/novo")
    public ResponseEntity<?> createCurso(@RequestBody @Valid CursoRequestDto data, UriComponentsBuilder uriBuilder) {

        Optional<Curso> possibleCurso = cursoService.findbyName(data.nome());

        if (possibleCurso.isPresent()) {
            return ResponseEntity.badRequest().body("Curso já cadastrado");
        }

        CursoResponseDto newCurso = cursoService.toDto(cursoService.createCurso(data));
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(newCurso.id()).toUri();

        return ResponseEntity.created(uri).body(newCurso);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Curso> possibleCurso = cursoService.findbyId(id);

        if (possibleCurso.isPresent()) {
            return ResponseEntity.ok(cursoService.toDto(possibleCurso.get()));
        }

        return ResponseEntity.notFound().build();
    }

}
