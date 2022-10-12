package ru.practicum.ewm.stat.dto;

import org.mapstruct.Mapper;
import ru.practicum.ewm.stat.Stat;

@Mapper(componentModel = "spring")
public interface StatMaper {

    Stat toStat(EndpointHit endpointHit);

    ViewStats toViewStats(Stat stat);

}
