package ru.practicum.entity.dto.comment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String text;
    private Long authorId;
    private Long eventId;
}
