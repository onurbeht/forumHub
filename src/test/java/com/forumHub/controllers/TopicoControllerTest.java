package com.forumHub.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.enums.Categoria;
import com.forumHub.domain.enums.Status;
import com.forumHub.dtos.curso.CursoResponseDto;
import com.forumHub.dtos.topico.TopicoPaginationResponseDto;
import com.forumHub.dtos.topico.TopicoRequestDto;
import com.forumHub.dtos.topico.TopicoResponseAllDto;
import com.forumHub.dtos.topico.TopicoResponseDto;
import com.forumHub.dtos.topico.TopicoUpdateDto;
import com.forumHub.services.CursoService;
import com.forumHub.services.TopicoService;
import com.forumHub.services.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicoControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper mapper;

        @MockBean
        TopicoService topicoService;

        @MockBean
        CursoService cursoService;

        @MockBean
        UsuarioService usuarioService;

        TopicoRequestDto topicoRequestDto;
        TopicoResponseDto topicoResponseDto;

        TopicoPaginationResponseDto topicoPaginationResponseDto;
        TopicoResponseAllDto topicoResponseAllDto;

        TopicoUpdateDto topicoUpdateDto;

        CursoResponseDto cursoResponseDto;

        Curso curso;
        Usuario usuario;
        Topico topico;

        @BeforeEach
        void setUp() {
                topicoRequestDto = new TopicoRequestDto("Titulo Topico", "Mensagem Topico", 1L);

                curso = Curso.builder().id(1L).nome("Nome curso").categoria(Categoria.BACKEND).build();
                cursoResponseDto = new CursoResponseDto(curso.getId(), curso.getNome(), curso.getCategoria());

                usuario = Usuario.builder().id("id usuario").username("username").build();

                topico = Topico.builder()
                                .id(99L)
                                .titulo("Titulo topico")
                                .mensagem("Mensagem topico")
                                .dataCriacao(LocalDateTime.now().minusDays(1))
                                .ativo(true)
                                .status(Status.ABERTA)
                                .autor(usuario)
                                .curso(curso)
                                .build();
        }

        @Test
        @DisplayName("Should create a new Topico and return TopicoResponseDto")
        @WithMockUser
        void TopicoController_createTopico_returnTopicoResponseDto() throws JsonProcessingException, Exception {

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem(),
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.ABERTA,
                                cursoResponseDto,
                                List.of());

                when(cursoService.findbyId(topicoRequestDto.idCurso())).thenReturn(Optional.of(curso));
                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.createTopico(topicoRequestDto, curso)).thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoRequestDto)));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(topicoResponseDto.id()))
                                .andExpect(jsonPath("$.idAutor").value(topicoResponseDto.idAutor()))
                                .andExpect(jsonPath("$.titulo").value(topicoResponseDto.titulo()))
                                .andExpect(jsonPath("$.mensagem").value(topicoResponseDto.mensagem()))
                                .andExpect(jsonPath("$.dataCriacao")
                                                .value(topicoResponseDto.dataCriacao().truncatedTo(ChronoUnit.MILLIS)
                                                                .toString()))
                                .andExpect(jsonPath("$.ativo").value(topicoResponseDto.ativo()))
                                .andExpect(jsonPath("$.status").value(topicoResponseDto.status().toString()))
                                .andExpect(jsonPath("$.curso.id").value(cursoResponseDto.id()))
                                .andExpect(jsonPath("$.curso.nome").value(cursoResponseDto.nome()))
                                .andExpect(jsonPath("$.curso.categoria").value(cursoResponseDto.categoria().toString()))
                                .andExpect(jsonPath("$.respostas").isArray())
                                .andExpect(jsonPath("$.respostas.length()")
                                                .value(topicoResponseDto.respostas().size()));

                verify(cursoService, times(1)).findbyId(1L);
                verifyNoMoreInteractions(cursoService);

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem());
                verify(topicoService, times(1)).createTopico(topicoRequestDto, curso);
                verifyNoMoreInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not create a new Topico when idCurso in request data doesn't exist, return error message")
        @WithMockUser
        void TopicoController_createTopico_errorCase1() throws JsonProcessingException, Exception {

                String expectedResponse = "Id do Curso informado não existe. Verifique e tente novamente";

                when(cursoService.findbyId(topicoRequestDto.idCurso())).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoRequestDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(cursoService, times(1)).findbyId(1L);
                verifyNoMoreInteractions(cursoService);

                verifyNoInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not create a new Topico when Topico already exist with the same title and message, return error message")
        @WithMockUser
        void TopicoController_createTopico_errorCase2() throws JsonProcessingException, Exception {

                String expectedResponse = "Topico ja cadastrado. Id do topico: " + topico.getId();

                when(cursoService.findbyId(topicoRequestDto.idCurso())).thenReturn(Optional.of(curso));
                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem())).thenReturn(Optional.of(topico));

                ResultActions response = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoRequestDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(cursoService, times(1)).findbyId(1L);
                verifyNoMoreInteractions(cursoService);

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem());
                verifyNoMoreInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not create a new Topico when request data is invalid, return error message")
        @WithMockUser
        void TopicoController_createTopico_errorCase3() throws JsonProcessingException, Exception {

                String expectedResponse1 = "must not be blank";
                String expectedResponse2 = "must not be null";

                TopicoRequestDto req1 = new TopicoRequestDto("", topicoRequestDto.mensagem(),
                                topicoRequestDto.idCurso());
                TopicoRequestDto req2 = new TopicoRequestDto(topicoRequestDto.titulo(), "", topicoRequestDto.idCurso());
                TopicoRequestDto req3 = new TopicoRequestDto(topicoRequestDto.titulo(), topicoRequestDto.mensagem(),
                                null);

                ResultActions response1 = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(req1)));

                ResultActions response2 = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(req2)));

                ResultActions response3 = mockMvc.perform(
                                post("/topicos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(req3)));

                response1
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("titulo"))
                                .andExpect(jsonPath("$[0].message").value(expectedResponse1));

                response2
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("mensagem"))
                                .andExpect(jsonPath("$[0].message").value(expectedResponse1));

                response3
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$[0].field").value("idCurso"))
                                .andExpect(jsonPath("$[0].message").value(expectedResponse2));

                verifyNoInteractions(cursoService);
                verifyNoInteractions(topicoService);

        }

        @Test
        @DisplayName("Should get all Topico with pagination, return TopicoPaginationResponseDto")
        @WithMockUser
        void TopicoController_getAll_returnTopicoPaginationResponseDto() throws JsonProcessingException, Exception {

                topicoResponseAllDto = new TopicoResponseAllDto(topico.getId(), topico.getAutor().getId(),
                                topico.getTitulo(),
                                topico.getMensagem(), topico.getDataCriacao().truncatedTo(ChronoUnit.MILLIS), true,
                                Status.ABERTA,
                                cursoResponseDto, 0);

                TopicoResponseAllDto topicoResponseAllDto2 = new TopicoResponseAllDto(2l, topico.getAutor().getId(),
                                topico.getTitulo(), topico.getMensagem(),
                                topico.getDataCriacao().truncatedTo(ChronoUnit.MILLIS), true,
                                Status.ABERTA, cursoResponseDto, 0);

                topicoPaginationResponseDto = new TopicoPaginationResponseDto(
                                List.of(topicoResponseAllDto, topicoResponseAllDto2), 1, 2, 10, 0, true, true);

                when(topicoService.getAll(any())).thenReturn(topicoPaginationResponseDto);

                ResultActions response = mockMvc.perform(
                                get("/topicos")
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].id").value(topicoResponseAllDto.id()))
                                .andExpect(jsonPath("$.content[1].id").value(topicoResponseAllDto2.id()))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.totalElements").value(2))
                                .andExpect(jsonPath("$.size").value(10))
                                .andExpect(jsonPath("$.number").value(0))
                                .andExpect(jsonPath("$.first").value(true))
                                .andExpect(jsonPath("$.last").value(true));

                verify(topicoService, times(1)).getAll(any());

                verifyNoMoreInteractions(topicoService);

        }

        @Test
        @DisplayName("Should get Topico by Id, return TopicoResponseDto")
        @WithMockUser
        void TopicoController_getById_returnTopicoResponseDto() throws JsonProcessingException, Exception {

                Long id = 1l;

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                topicoRequestDto.titulo(),
                                topicoRequestDto.mensagem(),
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.ABERTA,
                                cursoResponseDto,
                                List.of());

                when(topicoService.findById(id)).thenReturn(Optional.of(topico));
                when(topicoService.toTopicoResponseDto(topico)).thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                get("/topicos/{id}", id)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(topicoResponseDto.id()))
                                .andExpect(jsonPath("$.idAutor").value(topicoResponseDto.idAutor()))
                                .andExpect(jsonPath("$.titulo").value(topicoResponseDto.titulo()))
                                .andExpect(jsonPath("$.mensagem").value(topicoResponseDto.mensagem()))
                                .andExpect(jsonPath("$.dataCriacao")
                                                .value(topicoResponseDto.dataCriacao().truncatedTo(ChronoUnit.MILLIS)
                                                                .toString()))
                                .andExpect(jsonPath("$.ativo").value(topicoResponseDto.ativo()))
                                .andExpect(jsonPath("$.status").value(topicoResponseDto.status().toString()))
                                .andExpect(jsonPath("$.curso.id").value(cursoResponseDto.id()))
                                .andExpect(jsonPath("$.curso.nome").value(cursoResponseDto.nome()))
                                .andExpect(jsonPath("$.curso.categoria").value(cursoResponseDto.categoria().toString()))
                                .andExpect(jsonPath("$.respostas").isArray())
                                .andExpect(jsonPath("$.respostas.length()")
                                                .value(topicoResponseDto.respostas().size()));

                verify(topicoService, times(1)).findById(id);
                verify(topicoService, times(1)).toTopicoResponseDto(topico);

                verifyNoMoreInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not get Topico by Id when id doesn't exist, return 404 - Not Found")
        @WithMockUser
        void TopicoController_getById_erroCase1() throws JsonProcessingException, Exception {

                Long id = 10l;

                when(topicoService.findById(id)).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                get("/topicos/{id}", id)
                                                .contentType(MediaType.APPLICATION_JSON));

                response.andExpect(status().isNotFound());

                verify(topicoService, times(1)).findById(id);
                verifyNoMoreInteractions(topicoService);

        }

        @Test
        @DisplayName("Should update a Topico with all params of dto, return TopicoResponseDto")
        @WithMockUser
        void TopicoController_updateTopico_returnTopicoResponseDtoCase1() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("Novo titulo", "Nova mensagem", 2L);

                Curso newCurso = Curso.builder().id(2L).nome("Novo Curso").categoria(Categoria.INFRA).build();
                CursoResponseDto cursoToUpdateTopicoDto = new CursoResponseDto(2L, "Novo Curso", Categoria.INFRA);

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                "Novo titulo",
                                "Nova mensagem",
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.ABERTA,
                                cursoToUpdateTopicoDto,
                                List.of());

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
                when(cursoService.findbyId(topicoUpdateDto.idCurso())).thenReturn(Optional.of(newCurso));
                when(topicoService.updateTopico(topicoUpdateDto.titulo(), topicoUpdateDto.mensagem(), newCurso, topico))
                                .thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titulo").value(topicoUpdateDto.titulo()))
                                .andExpect(jsonPath("$.mensagem").value(topicoUpdateDto.mensagem()))
                                .andExpect(jsonPath("$.curso.id").value(cursoToUpdateTopicoDto.id()))
                                .andExpect(jsonPath("$.curso.nome").value(cursoToUpdateTopicoDto.nome()))
                                .andExpect(jsonPath("$.curso.categoria")
                                                .value(cursoToUpdateTopicoDto.categoria().toString()));

                assertNotEquals(topico.getTitulo(), topicoResponseDto.titulo());
                assertNotEquals(topico.getMensagem(), topicoResponseDto.mensagem());
                assertNotEquals(topico.getCurso().getId(), topicoResponseDto.curso().id());

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);
                verify(topicoService, times(1)).updateTopico(topicoUpdateDto.titulo(), topicoUpdateDto.mensagem(),
                                newCurso,
                                topico);
                verifyNoMoreInteractions(topicoService);

                verify(cursoService, times(1)).findbyId(topicoUpdateDto.idCurso());
                verifyNoMoreInteractions(cursoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should update a Topico with only title and message as params of dto, return TopicoResponseDto")
        @WithMockUser
        void TopicoController_updateTopico_returnTopicoResponseDtoCase2() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("Novo titulo", "Nova mensagem", null);

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                "Novo titulo",
                                "Nova mensagem",
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.ABERTA,
                                cursoResponseDto,
                                List.of());

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
                when(topicoService.updateTopico(topicoUpdateDto.titulo(), topicoUpdateDto.mensagem(), null, topico))
                                .thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titulo").value(topicoUpdateDto.titulo()))
                                .andExpect(jsonPath("$.mensagem").value(topicoUpdateDto.mensagem()))
                                .andExpect(jsonPath("$.curso.id").value(cursoResponseDto.id()))
                                .andExpect(jsonPath("$.curso.nome").value(cursoResponseDto.nome()))
                                .andExpect(jsonPath("$.curso.categoria")
                                                .value(cursoResponseDto.categoria().toString()));

                assertNotEquals(topico.getTitulo(), topicoResponseDto.titulo());
                assertNotEquals(topico.getMensagem(), topicoResponseDto.mensagem());
                assertEquals(topico.getCurso().getId(), cursoResponseDto.id());

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);
                verify(topicoService, times(1)).updateTopico(topicoUpdateDto.titulo(), topicoUpdateDto.mensagem(), null,
                                topico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should update a Topico with only one param of dto, return TopicoResponseDto")
        @WithMockUser
        void TopicoController_updateTopico_returnTopicoResponseDtoCase3() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("Novo titulo", null, null);

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                "Novo titulo",
                                topico.getMensagem(),
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.ABERTA,
                                cursoResponseDto,
                                List.of());

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
                when(topicoService.updateTopico(topicoUpdateDto.titulo(), null, null, topico))
                                .thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titulo").value(topicoUpdateDto.titulo()))
                                .andExpect(jsonPath("$.mensagem").value(topicoResponseDto.mensagem()))
                                .andExpect(jsonPath("$.curso.id").value(cursoResponseDto.id()))
                                .andExpect(jsonPath("$.curso.nome").value(cursoResponseDto.nome()))
                                .andExpect(jsonPath("$.curso.categoria")
                                                .value(cursoResponseDto.categoria().toString()));

                assertNotEquals(topico.getTitulo(), topicoResponseDto.titulo());
                assertEquals(topico.getMensagem(), topicoResponseDto.mensagem());
                assertEquals(topico.getCurso().getId(), cursoResponseDto.id());

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);
                verify(topicoService, times(1)).updateTopico(topicoUpdateDto.titulo(), null, null, topico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update a Topico when request body is empty, return error message")
        @WithMockUser
        void TopicoController_updateTopico_errorCase1() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                String expectedResponse = "Nenhum parametro, para atualização foi passado. Verifique e tente novamente.";

                topicoUpdateDto = new TopicoUpdateDto(null, null, null);

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verifyNoInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update a Topico when already exists a Topico with the same title and message, return error message")
        @WithMockUser
        void TopicoController_updateTopico_errorCase2() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("titulo", "mensage", null);

                String expectedMessage = "Já existe um topico com esse titulo e mensagem. ID do Topico:  "
                                + topico.getId()
                                + " \n Caso sua duvida seja diferente do Topico já cadastrado, altera o titulo ou a mensagem";

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.of(topico));

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedMessage));

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update a Topico when Topico doesn't exist, return error message")
        @WithMockUser
        void TopicoController_updateTopico_errorCase3() throws JsonProcessingException, Exception {

                Long idTopico = 10L;

                topicoUpdateDto = new TopicoUpdateDto("Novo titulo", null, null);

                String expectedResponse = "Informe um ID do topico valido.";

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update a Topico when Topico.autorId is different of current user.id, return error message")
        @WithMockUser
        void TopicoController_updateTopico_errorCase4() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("Novo titulo", null, null);

                Usuario otherUser = Usuario.builder().id("outro ID").username("outro").build();

                String expectedResponse = "Não é possivel alterar um Topico que não seja seu. Verifique o id informado.";

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(otherUser.getUsername());
                when(usuarioService.findByUsername(otherUser.getUsername())).thenReturn(Optional.of(otherUser));

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);

                verifyNoInteractions(cursoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(otherUser.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update a Topico when param id is invalid, return error message")
        @WithMockUser
        void RespostasController_updateTopico_errorCase5() throws JsonProcessingException, Exception {

                String idTopico = "abc";

                String expectedResponse = "Parametro informado está em um formato invalido, verifique e tente novamente";

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.field").value("id"))
                                .andExpect(jsonPath("$.message").value(expectedResponse));

                verifyNoInteractions(usuarioService);
                verifyNoInteractions(cursoService);
                verifyNoInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not update a Topico when idCurso doesn't exist, return error message")
        @WithMockUser
        void TopicoController_updateTopico_errorCase5() throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoUpdateDto = new TopicoUpdateDto("titulo", "mensage", 20L);

                String expectedMessage = "Id do Curso informado não existe, verifique e tente novamente";

                when(topicoService.findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem())).thenReturn(Optional.empty());
                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
                when(cursoService.findbyId(topicoUpdateDto.idCurso())).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                put("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(topicoUpdateDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedMessage));

                verify(topicoService, times(1)).findByTituloAndMensagemIgnoreCaseAtivoTrue(topicoUpdateDto.titulo(),
                                topicoUpdateDto.mensagem());
                verify(topicoService, times(1)).findById(idTopico);
                verifyNoMoreInteractions(topicoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);

                verify(cursoService, times(1)).findbyId(topicoUpdateDto.idCurso());
                verifyNoMoreInteractions(cursoService);
        }

        @Test
        @DisplayName("Should delete a Topico, return status code NotContent")
        @WithMockUser
        void TopicoController_deleteTopico_returnStatusCodeNoContent() throws JsonProcessingException, Exception {

                Long idTopico = 99L;

                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

                doAnswer(invocation -> {
                        topico.setAtivo(false);
                        return null;
                }).when(topicoService).delete(topico);

                ResultActions response = mockMvc.perform(
                                delete("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response.andExpect(status().isNoContent());

                assertEquals(false, topico.isAtivo());
                System.out.println(topico.isAtivo());

                verify(topicoService, times(1)).findById(idTopico);
                verify(topicoService, times(1)).delete(topico);
                verifyNoMoreInteractions(topicoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not delete a Topico when Topico doesn't exist, return error message")
        @WithMockUser
        void TopicoController_deleteTopico_errorCase1() throws JsonProcessingException, Exception {

                Long idTopico = 20L;

                String expectedResponse = "Informe um ID do topico valido.";

                when(topicoService.findById(idTopico)).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                delete("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                assertEquals(true, topico.isAtivo());

                verify(topicoService, times(1)).findById(idTopico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(usuarioService);

        }

        @Test
        @DisplayName("Should not delete a Topico when param id is invalid, return error message")
        @WithMockUser
        void RespostasController_deleteTopico_errorCase2() throws JsonProcessingException, Exception {

                String idTopico = "abc";

                String expectedResponse = "Parametro informado está em um formato invalido, verifique e tente novamente";

                ResultActions response = mockMvc.perform(
                                delete("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));
                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.field").value("id"))
                                .andExpect(jsonPath("$.message").value(expectedResponse));

                verifyNoInteractions(usuarioService);
                verifyNoInteractions(cursoService);
                verifyNoInteractions(topicoService);

        }

        @Test
        @DisplayName("Should not delete a Topico when Topico.autorId is different of curret user.id, return error message")
        @WithMockUser
        void TopicoController_deleteTopico_errorCase3() throws JsonProcessingException, Exception {

                Long idTopico = 99L;

                Usuario otherUser = Usuario.builder().id("Other ID").username("other").build();

                String expectedResponse = "Não é possivel deletar um Topico que não seja seu. Verifique o id informado.";

                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(otherUser.getUsername());
                when(usuarioService.findByUsername(otherUser.getUsername())).thenReturn(Optional.of(otherUser));

                ResultActions response = mockMvc.perform(
                                delete("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(topicoService, times(1)).findById(idTopico);
                verifyNoMoreInteractions(topicoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(otherUser.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not delete a Topico when Topico already is deleted, return error message")
        @WithMockUser
        void TopicoController_deleteTopico_errorCase4() throws JsonProcessingException, Exception {

                Long idTopico = 99L;

                topico.setAtivo(false);

                String expectedResponse = "Topico já deletado";

                when(topicoService.findById(idTopico)).thenReturn(Optional.of(topico));

                ResultActions response = mockMvc.perform(
                                delete("/topicos/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(topicoService, times(1)).findById(idTopico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should update the Status from Topico to RESPONDIDA, return TopicoResponseDto")
        @WithMockUser
        void TopicoController_updateStatusToRespondido_returnTopicoResponseDto()
                        throws JsonProcessingException, Exception {

                Long idTopico = 1L;

                topicoResponseDto = new TopicoResponseDto(
                                1L,
                                "Id autor",
                                "Novo titulo",
                                "Nova mensagem",
                                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                                true,
                                Status.RESPONDIDA,
                                null,
                                List.of());

                when(topicoService.findByIdAndStatus(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
                when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
                when(topicoService.updateStatusToRespondido(topico)).thenReturn(topicoResponseDto);

                ResultActions response = mockMvc.perform(
                                put("/topicos/changeStatus/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(topicoResponseDto.status().toString()));

                verify(topicoService, times(1)).findByIdAndStatus(idTopico);
                verify(topicoService, times(1)).updateStatusToRespondido(topico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(cursoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update the Status from Topico when Topico doesn'n exist or Status already is RESPONDIDA")
        @WithMockUser
        void TopicoController_updateStatusToRespondido_errorCase1() throws JsonProcessingException, Exception {

                Long idTopico = 10L;

                String expectedMessage = "Topico já está com o Status: RESPONDIDO";

                when(topicoService.findByIdAndStatus(idTopico)).thenReturn(Optional.empty());

                ResultActions response = mockMvc.perform(
                                put("/topicos/changeStatus/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedMessage));

                verify(topicoService, times(1)).findByIdAndStatus(idTopico);
                verifyNoMoreInteractions(topicoService);

                verifyNoInteractions(usuarioService);
        }

        @Test
        @DisplayName("Should not update the Status from Topico when Topico.autorId is different of curret user.id, return error message")
        @WithMockUser
        void TopicoController_updateStatusToRespondido_errorCase2() throws JsonProcessingException, Exception {

                Long idTopico = 10L;

                Usuario otherUsuario = Usuario.builder().username("NOME").id("OTHER ID").build();

                String expectedMessage = "Não é possivel alterar um Topico que não seja seu. Verifique o id informado.";

                when(topicoService.findByIdAndStatus(idTopico)).thenReturn(Optional.of(topico));
                when(usuarioService.getPrincipal()).thenReturn(otherUsuario.getUsername());
                when(usuarioService.findByUsername(otherUsuario.getUsername())).thenReturn(Optional.of(otherUsuario));

                ResultActions response = mockMvc.perform(
                                put("/topicos/changeStatus/{id}", idTopico)
                                                .contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedMessage));

                verify(topicoService, times(1)).findByIdAndStatus(idTopico);
                verifyNoMoreInteractions(topicoService);

                verify(usuarioService, times(1)).getPrincipal();
                verify(usuarioService, times(1)).findByUsername(otherUsuario.getUsername());
                verifyNoMoreInteractions(usuarioService);
        }

}
