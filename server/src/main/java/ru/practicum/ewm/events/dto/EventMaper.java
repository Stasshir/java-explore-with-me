package ru.practicum.ewm.events.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.utils.Utils;

@Mapper(componentModel = "spring", uses = {Utils.class})
public interface EventMaper {


    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "lon", source = "location.lon")
    Event toEvents(NewEventDto newEventDto);

    @Mapping(target = "location.lat", source = "lat")
    @Mapping(target = "location.lon", source = "lon")
    @Mapping(target = "confirmedRequests", expression = "java(event.getConfirmedRequests())")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", expression = "java(event.getConfirmedRequests())")
    EventShortDto toEventShortDto(Event event);

    Event toEvents(UpdateEventRequest updateEventRequest);

}
