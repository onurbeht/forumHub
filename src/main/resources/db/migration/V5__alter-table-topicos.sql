alter table if exists topicos add constraint fk_autor_id foreign key (autor_id) references usuarios;
alter table if exists topicos add constraint fk_curso_id foreign key (curso_id) references cursos;