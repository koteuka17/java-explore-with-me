package ru.practicum.entity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdRequest {
    @NotNull
    private List<Long> requestIds;
    @NotBlank
    private String status;

}
