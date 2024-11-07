package com.forumHub.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.enums.Status;
import com.forumHub.domain.repositories.TopicoRepository;
import com.forumHub.dtos.topico.TopicoPaginationResponseDto;
import com.forumHub.dtos.topico.TopicoRequestDto;
import com.forumHub.dtos.topico.TopicoResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicoService {

    private final TopicoRepository topicoRepository;
    private final UsuarioService usuarioService;
    private final CursoService cursoService;
    private final RespostasService respostasService;

    public Optional<Topico> findByTituloAndMensagemIgnoreCase(String titulo, String mensagem) {
        return topicoRepository.findByTituloIgnoreCaseAndMensagemIgnoreCase(titulo, mensagem);
    }

    public Optional<Topico> findById(Long id) {
        return topicoRepository.findById(id);
    }

    @Transactional
    public TopicoResponseDto createTopico(TopicoRequestDto data, Curso curso) {

        // Get current Usuario for Topico and responseDto
        Usuario currentUsuario = usuarioService.findByUsername(usuarioService.getPrincipal()).get();

        // Create Entity
        Topico topico = Topico.builder()
                .titulo(data.titulo())
                .mensagem(data.mensagem())
                .dataCriacao(LocalDateTime.now())
                .status(Status.ABERTA)
                .autor(currentUsuario)
                .curso(curso)
                .build();

        // Save Entity
        Topico newTopico = topicoRepository.save(topico);

        // Return dto of entity
        return toDto(newTopico);

    }

    public TopicoPaginationResponseDto getAll(Pageable pagination) {

        Page<TopicoResponseDto> response = topicoRepository.findAll(pagination).map(this::toDto);

        return new TopicoPaginationResponseDto(response.getContent(), response.getTotalPages(),
                response.getTotalElements(), response.getSize(), response.getNumber(), response.isFirst(),
                response.isLast());

    }

    public TopicoResponseDto toDto(Topico topico) {
        return new TopicoResponseDto(
                topico.getId(),
                topico.getAutor().getId(),
                topico.getTitulo(),
                topico.getMensagem(),
                topico.getDataCriacao(),
                topico.getStatus(),
                cursoService.toDto(topico.getCurso()),
                topico.getRespostas().stream().map(respostasService::mapToDto).toList());
    }

}
