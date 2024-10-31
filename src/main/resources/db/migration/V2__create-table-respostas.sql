create table respostas (
    id varchar(255) not null,
    data_criacao timestamp(6),
    topico_id bigint,
    autor_id varchar(255),
    mensagem text not null, 
    primary key (id)
);