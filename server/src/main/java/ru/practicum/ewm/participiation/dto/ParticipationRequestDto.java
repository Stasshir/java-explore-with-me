package ru.practicum.ewm.participiation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private int id;
    private String created;
    private int event;
    private int requester;
    private Status status;
}
