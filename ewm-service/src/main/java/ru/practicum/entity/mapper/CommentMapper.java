package ru.practicum.entity.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.dto.comment.CommentRequestDto;
import ru.practicum.entity.dto.comment.CommentResponseDto;
import ru.practicum.entity.model.Comment;
import ru.practicum.entity.model.Event;
import ru.practicum.entity.model.User;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class CommentMapper {
    public static Comment toEntity(CommentRequestDto commentDto, User author, Event event) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .event(event)
                .build();
    }

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .build();
    }

    public static List<CommentResponseDto> toDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).collect(toList());
    }
}
