package com.forumHub.services;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.repositories.UsuarioRepository;
import com.forumHub.dtos.usuario.CreateUsuarioDto;
import com.forumHub.dtos.usuario.LoginRequestDto;
import com.forumHub.infra.security.TokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario create(CreateUsuarioDto data) {

        String passwordEncoded = passwordEncoder.encode(data.password());

        var usuario = Usuario.builder().username(data.username()).password(passwordEncoded).build();

        return usuarioRepository.save(usuario);
    }

    public String login(LoginRequestDto data) {
        Usuario user = findByUsername(data.username()).get();

        return tokenService.generateToken(user);
    }

    public boolean verifyPassword(String username, String password) {
        Usuario user = findByUsername(username).get();

        return passwordEncoder.matches(password, user.getPassword());
    }

    public String getPrincipal() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
