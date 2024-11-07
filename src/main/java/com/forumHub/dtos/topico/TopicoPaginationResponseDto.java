package com.forumHub.dtos.topico;

import java.util.List;

public record TopicoPaginationResponseDto(
                List<TopicoResponseAllDto> content,
                int totalPages,
                long totalElements,
                int size,
                int number,
                boolean first,
                boolean last) {

}
