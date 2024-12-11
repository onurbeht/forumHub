package com.forumHub.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forumHub.services.TopicoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/topicos")
@RequiredArgsConstructor
public class AdminTopicoController {

    private final TopicoService topicoService;

    @GetMapping
    String teste() {
        return "ADMIN CONTROLLER";
    }

    @DeleteMapping("/{id}")
    @Transactional
    ResponseEntity<?> deleteTopico(@PathVariable Long id) {
        var possibleTopico = topicoService.findById(id);

        if (possibleTopico.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum topico com o ID informado");
        }

        if (!possibleTopico.get().isAtivo()) {
            return ResponseEntity.badRequest().body("Topico já excluido");
        }

        topicoService.delete(possibleTopico.get());

        return ResponseEntity.noContent().build();

    }

}
