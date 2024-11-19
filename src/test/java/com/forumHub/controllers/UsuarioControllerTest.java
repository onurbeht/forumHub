package com.forumHub.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.forumHub.domain.entities.Usuario;
import com.forumHub.dtos.usuario.CreateUsuarioDto;
import com.forumHub.dtos.usuario.CreateUsuarioResponseDto;
import com.forumHub.dtos.usuario.LoginRequestDto;
import com.forumHub.dtos.usuario.LoginResponseDto;
import com.forumHub.dtos.usuario.UsuarioResponseDto;
import com.forumHub.infra.exceptions.WrongPasswordException;
import com.forumHub.services.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UsuarioService usuarioService;

    Usuario usuario;

    CreateUsuarioDto createUsuarioRequestDto;

    CreateUsuarioResponseDto createUsuarioResponseDto;

    LoginRequestDto loginRequestDto;

    LoginResponseDto loginResponseDto;

    UsuarioResponseDto usuarioResponseDto;

    @BeforeEach
    void setUp() {

        usuario = Usuario.builder().id(UUID.randomUUID().toString()).username("username").password("password").build();

        createUsuarioRequestDto = new CreateUsuarioDto("username", "password", "password");

        loginRequestDto = new LoginRequestDto("username", "password");
    }

    @Test
    @DisplayName("Should create a new Usuario with success and return the Usuario created")
    void UsuarioController_create_returnUsuario() throws JsonProcessingException, Exception {

        createUsuarioResponseDto = new CreateUsuarioResponseDto(usuario.getId(), createUsuarioRequestDto.username());

        when(usuarioService.findByUsername(createUsuarioRequestDto.username())).thenReturn(Optional.empty());
        when(usuarioService.create(createUsuarioRequestDto)).thenReturn(createUsuarioResponseDto);

        ResultActions response = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUsuarioRequestDto)));

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createUsuarioResponseDto.id()))
                .andExpect(jsonPath("$.username").value(createUsuarioResponseDto.username()));

        verify(usuarioService, times(1)).findByUsername(createUsuarioRequestDto.username());
        verify(usuarioService, times(1)).create(createUsuarioRequestDto);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not create a new Usuario when username already exist, return error message")
    void UsuarioController_create_errorCase1() throws JsonProcessingException, Exception {

        String expectedResponse = "Usuario já existe";

        when(usuarioService.findByUsername(createUsuarioRequestDto.username())).thenReturn(Optional.of(usuario));

        ResultActions response = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUsuarioRequestDto)));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(usuarioService, times(1)).findByUsername(createUsuarioRequestDto.username());
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not create a new Usuario when passwords are not equals, return error message")
    void UsuarioController_create_errorCase2() throws JsonProcessingException, Exception {

        String expectedResponse = "Senhas não são iguais!";

        createUsuarioRequestDto = new CreateUsuarioDto("username", "password", "other password");

        ResultActions response = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUsuarioRequestDto)));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not create a new Usuario when request data is invalid, return error message")
    void UsuarioController_create_errorCase3() throws JsonProcessingException, Exception {

        CreateUsuarioDto req1 = new CreateUsuarioDto("", "password", "password");
        CreateUsuarioDto req2 = new CreateUsuarioDto("username", "", "password");
        CreateUsuarioDto req3 = new CreateUsuarioDto("username", "password", "");

        String expectedResponse = "must not be blank";

        ResultActions response1 = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req1)));

        ResultActions response2 = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req2)));

        ResultActions response3 = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req3)));

        response1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("username"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));
        response2
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("password"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));
        response3
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("confirmPassword"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should login with success and return the tokenJwt created")
    void UsuarioController_login_returnTokenJwt() throws JsonProcessingException,
            Exception {

        loginResponseDto = new LoginResponseDto("Token jwt ...");

        when(usuarioService.findByUsername(loginRequestDto.username())).thenReturn(Optional.of(usuario));
        when(usuarioService.login(loginRequestDto)).thenReturn("Token jwt ...");

        ResultActions response = mockMvc.perform(
                post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequestDto)));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(loginResponseDto.token()));

        verify(usuarioService, times(1)).findByUsername(loginRequestDto.username());
        verify(usuarioService, times(1)).login(loginRequestDto);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not login when username doesn`t exist, return error message")
    void UsuarioController_login_errorCase1() throws JsonProcessingException, Exception {

        String expectedResponse = "Usuario ou senha incorretos.";

        when(usuarioService.findByUsername(loginRequestDto.username())).thenReturn(Optional.empty());

        ResultActions response = mockMvc.perform(
                post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequestDto)));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(usuarioService, times(1)).findByUsername(loginRequestDto.username());
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not login when password doesn`t match, throw WrongPasswordException")
    void UsuarioController_login_errorCase2() throws JsonProcessingException, Exception {

        String expectedResponse = "Usuario ou senha incorretos.";

        when(usuarioService.findByUsername(loginRequestDto.username())).thenReturn(Optional.of(usuario));
        when(usuarioService.login(loginRequestDto))
                .thenThrow(new WrongPasswordException("Usuario ou senha incorretos."));

        ResultActions response = mockMvc.perform(
                post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequestDto)));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse));

        verify(usuarioService, times(1)).findByUsername(loginRequestDto.username());
        verify(usuarioService, times(1)).login(loginRequestDto);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should not login when request data is invalid, return error message")
    void UsuarioController_login_errorCase3() throws JsonProcessingException, Exception {

        LoginRequestDto req1 = new LoginRequestDto("", "password");
        LoginRequestDto req2 = new LoginRequestDto("username", "");

        String expectedResponse = "must not be blank";

        ResultActions response1 = mockMvc.perform(
                post("/usuario/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req1)));

        ResultActions response2 = mockMvc.perform(
                post("/usuario/novo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req2)));

        response1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("username"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));
        response2
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("password"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Should return current user by tokenJwt, return usuarioResponseDto ")
    @WithMockUser
    void UsuarioController_currentUsuario_returnUsuarioResponseDto() throws JsonProcessingException, Exception {

        usuarioResponseDto = new UsuarioResponseDto(usuario.getId(), usuario.getUsername(), List.of());

        when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
        when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
        when(usuarioService.toUsuarioResponseDto(usuario)).thenReturn(usuarioResponseDto);

        ResultActions response = mockMvc.perform(
                get("/usuario")
                        .contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioResponseDto.id()))
                .andExpect(jsonPath("$.username").value(usuarioResponseDto.username()))
                .andExpect(jsonPath("$.respostas").isArray())
                .andExpect(jsonPath("$.respostas.length()").value(usuarioResponseDto.respostas().size()));
    }
}
