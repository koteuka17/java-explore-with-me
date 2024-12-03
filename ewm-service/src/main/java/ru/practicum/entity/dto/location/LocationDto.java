package ru.practicum.entity.dto.location;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    @NotNull
    private Float lat;
    @NotNull
    private Float lon;
}
