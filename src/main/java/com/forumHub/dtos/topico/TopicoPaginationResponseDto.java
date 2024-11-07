package com.forumHub.dtos.topico;

import java.util.List;

public record TopicoPaginationResponseDto(
        List<TopicoResponseDto> content,
        int totalPages,
        long totalElements,
        int size,
        int number,
        boolean first,
        boolean last) {

}