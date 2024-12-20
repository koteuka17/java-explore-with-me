package ru.practicum.entity.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.entity.dto.location.LocationDto;
import ru.practicum.entity.util.notblanknull.NotBlankNull;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateEventRequest {
    @Length(min = 20, max = 2000)
    @NotBlankNull
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    @NotBlankNull
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Long participantLimit;
    private Boolean requestModeration;
    @Length(min = 3, max = 120)
    @NotBlankNull
    private String title;
}
