package com.forumHub.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forumHub.domain.entities.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByNomeIgnoreCase(String nome);

}
