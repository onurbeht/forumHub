package com.forumHub.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "respostas")
@Entity(name = "Respostas")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Respostas {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String mensagem;
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "topico_id")
    private Topico topico;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

}
