package com.forumHub.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forumHub.domain.entities.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

}
