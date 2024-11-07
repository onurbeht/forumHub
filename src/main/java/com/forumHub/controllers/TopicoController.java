package com.forumHub.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumHub.dtos.topico.TopicoPaginationResponseDto;
import com.forumHub.dtos.topico.TopicoRequestDto;
import com.forumHub.dtos.topico.TopicoResponseDto;
import com.forumHub.dtos.topico.TopicoUpdateDto;
import com.forumHub.services.CursoService;
import com.forumHub.services.TopicoService;
import com.forumHub.services.UsuarioService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("topicos")
@RequiredArgsConstructor
public class TopicoController {

    private final CursoService cursoService;
    private final TopicoService topicoService;
    private final UsuarioService usuarioService;

    // POST
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

    // GET
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

    // PUT
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateTopico(@PathVariable Long id, @RequestBody TopicoUpdateDto data) {

        if (data.titulo() == null && data.mensagem() == null && data.idCurso() == null) {
            return ResponseEntity.badRequest()
                    .body("Nenhum parametro, para atualização foi passado. Verifique e tente novamente.");
        }

        // Get Topico to update
        var possibleTopico = topicoService.findById(id);

        // Get current Usuario
        var currentUser = usuarioService.findByUsername(usuarioService.getPrincipal()).get();

        // Check if topico Exists
        if (possibleTopico.isEmpty()) {
            return ResponseEntity.badRequest().body("Informe um ID do topico valido.");
        }

        // Check if idAutor from Topico is the same of currentUser
        if (!possibleTopico.get().getAutor().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest()
                    .body("Não é possivel alterar um Topico que não seja seu. Verifique o id informado.");
        }

        // Declare response
        TopicoResponseDto response;

        // Check if idCurso != null
        if (data.idCurso() != null) {
            var possibleCurso = cursoService.findbyId(data.idCurso());

            // Check if curso exists
            if (possibleCurso.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Id do Curso informado não existe, verifique e tente novamente");
            }

            // If curso exists, call method to update and return
            response = topicoService.updateTopico(data.titulo(), data.mensagem(),
                    possibleCurso.get(),
                    possibleTopico.get());

            return ResponseEntity.ok(response);
        }

        response = topicoService.updateTopico(data.titulo(), data.mensagem(), null,
                possibleTopico.get());

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteTopico(@PathVariable Long id) {

        // Get Topico to update
        var possibleTopico = topicoService.findById(id);

        // Check if topico Exists
        if (possibleTopico.isEmpty()) {
            return ResponseEntity.badRequest().body("Informe um ID do topico valido.");
        }

        // Get current Usuario
        var currentUser = usuarioService.findByUsername(usuarioService.getPrincipal()).get();

        // Check if idAutor from Topico is the same of currentUser
        if (!possibleTopico.get().getAutor().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest()
                    .body("Não é possivel deletar um Topico que não seja seu. Verifique o id informado.");
        }

        // Check if topico ativo already is false
        if (!possibleTopico.get().isAtivo()) {
            return ResponseEntity.badRequest().body("Topico já deletado");
        }

        topicoService.delete(possibleTopico.get());

        return ResponseEntity.noContent().build();
    }

}
