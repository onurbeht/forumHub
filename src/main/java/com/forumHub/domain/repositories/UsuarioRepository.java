package com.forumHub.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forumHub.domain.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

}
