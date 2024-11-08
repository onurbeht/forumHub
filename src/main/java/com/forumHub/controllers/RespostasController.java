package com.forumHub.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.forumHub.dtos.respostas.RespostaResponseDto;
import com.forumHub.dtos.respostas.RespostasRequestDto;
import com.forumHub.services.RespostasService;
import com.forumHub.services.TopicoService;
import com.forumHub.services.UsuarioService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("respostas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class RespostasController {

    private final RespostasService respostasService;
    private final UsuarioService usuarioService;
    private final TopicoService topicoService;

    @PostMapping("/novo/{idTopico}")
    public ResponseEntity<?> createResposta(@PathVariable Long idTopico, @RequestBody @Valid RespostasRequestDto data,
            UriComponentsBuilder uriBuilder) {

        // Get topico by id and ativo true
        var possibleTopico = topicoService.findByIdAndAtivo(idTopico);

        if (possibleTopico.isEmpty()) {
            return ResponseEntity.badRequest().body("Id do topico informado não existe. Verifique e tente novamente");
        }

        // Get currentUser
        var currentUser = usuarioService.findByUsername(usuarioService.getPrincipal()).get();

        RespostaResponseDto response = respostasService.createResposta(possibleTopico.get(), currentUser,
                data.resposta());

        var uri = uriBuilder.path("/topicos/{idTopico}").buildAndExpand(response.idTopico()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

}
