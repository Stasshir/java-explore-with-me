package ru.practicum.ewm.participiation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.participiation.Participation;
import ru.practicum.ewm.utils.Utils;

import java.util.List;

@Mapper(componentModel = "spring", uses = {Utils.class})
public interface ParticipationMaper {

    @Mapping(target = "event", expression = "java(participation.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(participation.getRequester().getId())")
    ParticipationRequestDto toParticipationRequestDto(Participation participation);

    List<ParticipationRequestDto> toListParticipationRequestDto(List<Participation> participation);
}
