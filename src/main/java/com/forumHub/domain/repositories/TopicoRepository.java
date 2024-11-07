package com.forumHub.domain.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.forumHub.domain.entities.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    Optional<Topico> findByTituloIgnoreCaseAndMensagemIgnoreCase(String titulo, String mensagem);

    @NonNull
    Page<Topico> findAll(@NonNull Pageable pageable);

}
