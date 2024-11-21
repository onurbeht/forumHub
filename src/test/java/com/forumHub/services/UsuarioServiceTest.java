package com.forumHub.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.repositories.UsuarioRepository;
import com.forumHub.dtos.usuario.CreateUsuarioDto;
import com.forumHub.dtos.usuario.CreateUsuarioResponseDto;
import com.forumHub.dtos.usuario.LoginRequestDto;
import com.forumHub.dtos.usuario.UsuarioResponseDto;
import com.forumHub.infra.exceptions.WrongPasswordException;
import com.forumHub.infra.security.TokenService;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    TokenService tokenService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RespostasService respostasService;

    @InjectMocks
    UsuarioService usuarioService;

    Usuario usuario;

    CreateUsuarioDto createUsuarioDto;
    CreateUsuarioResponseDto createUsuarioResponseDto;

    LoginRequestDto loginRequestDto;

    UsuarioResponseDto usuarioResponseDto;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id("Id usuario").username("username").build();
    }

    @Test
    @DisplayName("Should find a Usuario by username")
    void UsuarioService_findByUsername() {
        String username = "username";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));

        Optional<Usuario> searchedUser = usuarioService.findByUsername(username);

        assertTrue(searchedUser.isPresent());
        assertEquals(searchedUser.get().getUsername(), username);
        assertEquals(searchedUser.get(), usuario);
    }

    @Test
    @DisplayName("Should not find a Usuario by username")
    void UsuarioService_findByUsername_errorCase1() {
        String username = "abc";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Usuario> searchedUser = usuarioService.findByUsername(username);

        assertTrue(searchedUser.isEmpty());
    }

    @Test
    @DisplayName("Should create an Usuario, return UsuarioResponseDto")
    void UsuarioService_create_returnUsuarioResponseDto() {
        // Request
        createUsuarioDto = new CreateUsuarioDto("novo", "senha", "senha");

        usuario.setPassword("encodedPassword");

        // Response
        createUsuarioResponseDto = new CreateUsuarioResponseDto(usuario.getId(), usuario.getUsername());

        when(passwordEncoder.encode(createUsuarioDto.password())).thenReturn("encodedPassword");
        when(usuarioRepository.save(
                Usuario.builder()
                        .username(createUsuarioDto.username())
                        .password("encodedPassword")
                        .build()))
                .thenReturn(usuario);

        CreateUsuarioResponseDto response = usuarioService.create(createUsuarioDto);

        assertEquals(createUsuarioResponseDto, response);

        verify(usuarioRepository, times(1)).save(Usuario.builder()
                .username(createUsuarioDto.username())
                .password("encodedPassword")
                .build());

        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Should login with success and return Token Jwt")
    void UsuarioService_login_returnTokenJwt() {

        loginRequestDto = new LoginRequestDto("username", "senha");
        usuario.setPassword("ecodedPassword");

        String tokenJwt = "abc";

        when(tokenService.generateToken(usuario)).thenReturn("abc");
        when(passwordEncoder.matches(loginRequestDto.password(), usuario.getPassword())).thenReturn(true);

        String response = usuarioService.login(loginRequestDto, usuario);

        assertEquals(tokenJwt, response);

        verify(tokenService, times(1)).generateToken(usuario);
        verify(passwordEncoder, times(1)).matches(loginRequestDto.password(), usuario.getPassword());

    }

    @Test
    @DisplayName("Should not login with success and throw Exception")
    void UsuarioService_login_throwException() {

        loginRequestDto = new LoginRequestDto("username", "senha");
        usuario.setPassword("ecodedPassword");

        when(passwordEncoder.matches(loginRequestDto.password(), usuario.getPassword()))
                .thenThrow(new WrongPasswordException("Usuario ou senha incorretos."));

        assertThrows(WrongPasswordException.class, () -> {
            usuarioService.login(loginRequestDto, usuario);
        }, "Usuario ou senha incorretos.");

        verify(passwordEncoder, times(1)).matches(loginRequestDto.password(), usuario.getPassword());

    }

    @Test
    @DisplayName("Should verify password and return true when passwords are the same")
    void UsuarioService_verifyPassword_returnTrue() {
        String rawPassword = "abc";
        String hashedPassword = "abc";

        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        assertTrue(usuarioService.verifyPassword(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Should verify password and return true when passwords aren't the same")
    void UsuarioService_verifyPassword_returnFalse() {
        String rawPassword = "abc";
        String hashedPassword = "def";

        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        assertFalse(usuarioService.verifyPassword(rawPassword, hashedPassword));
    }

    @Test
    @DisplayName("Map to UsuarioResponseDto")
    void UsuarioService_toUsuarioResponseDto() {
        usuarioResponseDto = new UsuarioResponseDto(usuario.getId(), usuario.getUsername(), List.of());

        assertEquals(usuarioService.toUsuarioResponseDto(usuario), usuarioResponseDto);
    }

}
