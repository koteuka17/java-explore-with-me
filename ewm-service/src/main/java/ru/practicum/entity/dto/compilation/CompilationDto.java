package ru.practicum.entity.dto.compilation;

import lombok.*;
import ru.practicum.entity.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Set<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;
}
