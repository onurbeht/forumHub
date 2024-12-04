package com.forumHub.domain.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.enums.Status;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    Optional<Topico> findByTituloIgnoreCaseAndMensagemIgnoreCaseAndAtivoTrue(String titulo, String mensagem);

    Optional<Topico> findByIdAndAtivoTrue(Long id);

    Optional<Topico> findByIdAndStatus(Long id, Status status);

    @NonNull
    Page<Topico> findAllByAtivoTrue(@NonNull Pageable pageable);

}
