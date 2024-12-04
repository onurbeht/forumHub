package com.forumHub.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.entities.Respostas;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.enums.Categoria;
import com.forumHub.domain.enums.Status;
import com.forumHub.domain.repositories.TopicoRepository;
import com.forumHub.dtos.curso.CursoResponseDto;
import com.forumHub.dtos.respostas.RespostaResponseDto;
import com.forumHub.dtos.topico.TopicoPaginationResponseDto;
import com.forumHub.dtos.topico.TopicoRequestDto;
import com.forumHub.dtos.topico.TopicoResponseAllDto;
import com.forumHub.dtos.topico.TopicoResponseDto;

@ExtendWith(MockitoExtension.class)
public class TopicoServiceTest {

    @Mock
    TopicoRepository topicoRepository;
    @Mock
    UsuarioService usuarioService;
    @Mock
    CursoService cursoService;
    @Mock
    RespostasService respostasService;

    @InjectMocks
    TopicoService topicoService;

    Topico topico;
    Usuario usuario;
    Curso curso;
    Respostas respostas;

    TopicoRequestDto topicoRequestDto;
    TopicoResponseDto topicoResponseDto;
    TopicoPaginationResponseDto topicoPaginationResponseDto;
    TopicoResponseAllDto topicoResponseAllDto;

    CursoResponseDto cursoResponseDto;
    RespostaResponseDto respostaResponseDto;

    @BeforeEach
    void setUp() {
        curso = Curso.builder().id(1L).nome("Nome curso").categoria(Categoria.BACKEND).build();

        usuario = Usuario.builder().id("Id usuario").username("Username").build();

        topico = Topico.builder()
                .id(99L)
                .titulo("Titulo topico")
                .mensagem("Mensagem topico")
                .dataCriacao(LocalDateTime.now())
                .ativo(true)
                .status(Status.ABERTA)
                .autor(usuario)
                .curso(curso)
                .build();

        respostas = Respostas.builder()
                .id("resposta id")
                .autor(usuario)
                .dataCriacao(LocalDateTime.now())
                .autor(usuario)
                .topico(topico)
                .build();

        cursoResponseDto = new CursoResponseDto(curso.getId(), curso.getNome(), curso.getCategoria());

        respostaResponseDto = new RespostaResponseDto(respostas.getId(), respostas.getMensagem(),
                respostas.getDataCriacao(), respostas.getTopico().getId(), respostas.getAutor().getId());
    }

    @Test
    @DisplayName("Should find a topico byTituloAndMensagemIgnoreCaseAtivoTrue")
    void TopicoService_findByTituloAndMensagemIgnoreCaseAtivoTrue() {

        String titulo = "Titulo topico";
        String mensagem = "Mensagem topico";

        when(topicoRepository.findByTituloIgnoreCaseAndMensagemIgnoreCaseAndAtivoTrue(titulo, mensagem))
                .thenReturn(Optional.of(topico));

        Optional<Topico> response = topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(titulo, mensagem);

        assertTrue(response.isPresent());
        assertEquals(response.get(), topico);

        verify(topicoRepository, times(1)).findByTituloIgnoreCaseAndMensagemIgnoreCaseAndAtivoTrue(titulo, mensagem);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should not find a topico byTituloAndMensagemIgnoreCaseAtivoTrue")
    void TopicoService_findByTituloAndMensagemIgnoreCaseAtivoTrue_notFind() {

        String titulo = "abc";
        String mensagem = "xyz";

        when(topicoRepository.findByTituloIgnoreCaseAndMensagemIgnoreCaseAndAtivoTrue(titulo, mensagem))
                .thenReturn(Optional.empty());

        Optional<Topico> response = topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(titulo, mensagem);

        assertTrue(response.isEmpty());

        verify(topicoRepository, times(1)).findByTituloIgnoreCaseAndMensagemIgnoreCaseAndAtivoTrue(titulo, mensagem);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should find by Id")
    void TopicoService_findById() {

        Long id = 99L;

        when(topicoRepository.findById(id))
                .thenReturn(Optional.of(topico));

        Optional<Topico> response = topicoService.findById(id);

        assertTrue(response.isPresent());
        assertEquals(response.get(), topico);

        verify(topicoRepository, times(1)).findById(id);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should not find by Id")
    void TopicoService_findById_notFind() {

        Long id = 10L;

        when(topicoRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Topico> response = topicoService.findById(id);

        assertTrue(response.isEmpty());

        verify(topicoRepository, times(1)).findById(id);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should find by Id and ativo true")
    void TopicoService_findByIdAndAtivoTrue() {

        Long id = 99L;

        when(topicoRepository.findByIdAndAtivoTrue(id))
                .thenReturn(Optional.of(topico));

        Optional<Topico> response = topicoService.findByIdAndAtivo(id);

        assertTrue(response.isPresent());
        assertEquals(response.get(), topico);

        verify(topicoRepository, times(1)).findByIdAndAtivoTrue(id);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should not find by Id and ativo true")
    void TopicoService_findByIdAndAtivotrue_notFind() {

        Long id = 99L;

        topico.setAtivo(false);

        when(topicoRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Optional<Topico> response = topicoService.findByIdAndAtivo(id);

        assertTrue(response.isEmpty());

        verify(topicoRepository, times(1)).findByIdAndAtivoTrue(id);

        verifyNoMoreInteractions(topicoRepository);

    }

    @Test
    @DisplayName("Should create a new Topico")
    void TopicoService_createTopico() {
        topicoRequestDto = new TopicoRequestDto("Novo topico", "mensage", 1l);

        topico.setTitulo(topicoRequestDto.titulo());
        topico.setMensagem(topicoRequestDto.mensagem());

        topicoResponseDto = new TopicoResponseDto(topico.getId(), topico.getAutor().getId(), topico.getTitulo(),
                topico.getMensagem(), topico.getDataCriacao(), topico.isAtivo(), topico.getStatus(), cursoResponseDto,
                List.of());

        when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
        when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
        when(cursoService.toDto(curso)).thenReturn(cursoResponseDto);
        when(topicoRepository.save(Topico.builder()
                .titulo("Titulo topico")
                .mensagem("Mensagem topico")
                .dataCriacao(LocalDateTime.now())
                .ativo(true)
                .status(Status.ABERTA)
                .autor(usuario)
                .curso(curso)
                .build())).thenReturn(topico);

        TopicoService topicoServiceSpy = spy(topicoService);

        TopicoResponseDto response = topicoServiceSpy.createTopico(topicoRequestDto, curso);

        assertEquals(topicoResponseDto, response);

        verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioService, times(1)).getPrincipal();

        verifyNoMoreInteractions(usuarioService);

        verify(topicoRepository, times(1)).save(Topico.builder()
                .titulo("Titulo topico")
                .mensagem("Mensagem topico")
                .dataCriacao(LocalDateTime.now())
                .ativo(true)
                .status(Status.ABERTA)
                .autor(usuario)
                .curso(curso)
                .build());

        verifyNoMoreInteractions(topicoRepository);

        verify(cursoService, times(1)).toDto(curso);

        verify(topicoServiceSpy, times(1)).toTopicoResponseDto(topico);

    }

    @Test
    @DisplayName("Should update a Topico")
    void TopicoService_updateTopico() {

        String titulo = "titulo update";
        String mensagem = "mensagem update";

        topicoResponseDto = new TopicoResponseDto(topico.getId(), topico.getAutor().getId(), titulo, mensagem,
                topico.getDataCriacao(), topico.isAtivo(), topico.getStatus(), cursoResponseDto, List.of());

        when(cursoService.toDto(curso)).thenReturn(cursoResponseDto);

        TopicoResponseDto response = topicoService.updateTopico(titulo, mensagem, curso, topico);

        assertEquals(topicoResponseDto, response);

    }

    @Test
    @DisplayName("Should delete a Topico")
    void TopicoService_delete() {

        topicoService.delete(topico);

        assertFalse(topico.isAtivo());

    }

    @Test
    @DisplayName("Should find by Id and Status ABERTO")
    void TopicoService_findByIdAndStatus() {
        Long id = 1L;
        Status status = Status.ABERTA;

        when(topicoRepository.findByIdAndStatus(id, status)).thenReturn(Optional.of(topico));

        Optional<Topico> response = topicoService.findByIdAndStatus(id);

        assertTrue(response.isPresent());
        assertEquals(topico, response.get());

        verify(topicoRepository, times(1)).findByIdAndStatus(id, status);
        verifyNoMoreInteractions(topicoRepository);
    }

    @Test
    @DisplayName("Should not find by Id and Status ABERTO")
    void TopicoService_findByIdAndStatus_notFind() {
        Long id = 10L;
        Status status = Status.ABERTA;

        when(topicoRepository.findByIdAndStatus(id, status)).thenReturn(Optional.empty());

        Optional<Topico> response = topicoService.findByIdAndStatus(id);

        assertTrue(response.isEmpty());

        verify(topicoRepository, times(1)).findByIdAndStatus(id, status);
        verifyNoMoreInteractions(topicoRepository);
    }

    @Test
    @DisplayName("Should update Status to RESPONDIDO")
    void TopicoService_updateStatusToRespondido() {

        topico.setStatus(Status.RESPONDIDA);

        topicoResponseDto = new TopicoResponseDto(topico.getId(), topico.getAutor().getId(), topico.getTitulo(),
                topico.getMensagem(),
                topico.getDataCriacao(), topico.isAtivo(), topico.getStatus(), null, List.of());

        TopicoResponseDto response = topicoService.updateStatusToRespondido(topico);

        assertEquals(response, topicoResponseDto);

        verifyNoInteractions(topicoRepository);
    }

}
