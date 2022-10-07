package ru.practicum.ewm.stat;


import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.util.Set;

public interface StatService {

    void saveStat(EndpointHit endpointHit);

    Set<ViewStats> getStat(String start, String end, String[] uris, boolean unique);
}
