package com.forumHub.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumHub.dtos.topico.TopicoPaginationResponseDto;
import com.forumHub.dtos.topico.TopicoRequestDto;
import com.forumHub.dtos.topico.TopicoResponseDto;
import com.forumHub.services.CursoService;
import com.forumHub.services.TopicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("topicos")
@RequiredArgsConstructor
public class TopicoController {

    private final CursoService cursoService;
    private final TopicoService topicoService;

    @PostMapping("/novo")
    public ResponseEntity<?> createTopico(@RequestBody @Valid TopicoRequestDto data) {

        // Check if the curso exists
        var possibleCurso = cursoService.findbyId(data.idCurso());

        if (possibleCurso.isEmpty()) {
            return ResponseEntity.badRequest().body("Id do Curso informado não existe. Verifique e tente novamente");
        }

        // Check if Topico already exists
        var possibleTopico = topicoService.findByTituloAndMensagemIgnoreCase(data.titulo(), data.mensagem());

        if (possibleTopico.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Topico ja cadastrado. Id do topico: " + possibleTopico.get().getId());
        }

        TopicoResponseDto responseDto = topicoService.createTopico(data, possibleCurso.get());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<TopicoPaginationResponseDto> getAll(
            @PageableDefault(size = 10, sort = { "dataCriacao" }, direction = Direction.DESC) Pageable pagination) {
        return ResponseEntity.ok(topicoService.getAll(pagination));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        var possibleTopico = topicoService.findById(id);

        if (possibleTopico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(topicoService.toDto(possibleTopico.get()));
    }
}
