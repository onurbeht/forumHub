package com.forumHub.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.forumHub.domain.entities.Usuario;
import com.forumHub.dtos.usuario.CreateUsuarioDto;
import com.forumHub.dtos.usuario.LoginRequestDto;
import com.forumHub.dtos.usuario.LoginResponseDto;
import com.forumHub.dtos.usuario.UsuarioResponseDto;
import com.forumHub.services.UsuarioService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/novo")
    public ResponseEntity<?> create(@RequestBody @Valid CreateUsuarioDto data, UriComponentsBuilder uriBuiler) {

        if (!data.password().equals(data.confirmPassword())) {
            return ResponseEntity.badRequest().body("Senhas não são iguais!");
        }

        var possibleUsuario = usuarioService.findByUsername(data.username());

        if (possibleUsuario.isPresent()) {
            return ResponseEntity.badRequest().body("Usuario já existe");
        }

        var newUsuario = usuarioService.create(data);
        var uri = uriBuiler.path("/usuario").build().toUri();

        return ResponseEntity.created(uri).body(newUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto data) {

        Optional<Usuario> possibleUsuario = usuarioService.findByUsername(data.username());

        if (possibleUsuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario ou senha incorretos.");
        }

        var token = usuarioService.login(data, possibleUsuario.get());

        return ResponseEntity.ok(new LoginResponseDto(token));

    }

    @GetMapping()
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<?> CurrentUsuario() {
        Usuario user = usuarioService.findByUsername(usuarioService.getPrincipal())
                .orElseThrow(() -> new RuntimeException("User must be loggend in"));

        UsuarioResponseDto response = usuarioService.toUsuarioResponseDto(user);

        return ResponseEntity.ok(response);
    }

}
