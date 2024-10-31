package com.forumHub.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forumHub.domain.entities.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

}
