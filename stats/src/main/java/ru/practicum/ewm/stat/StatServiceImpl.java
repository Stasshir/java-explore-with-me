package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.StatMaper;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final StatMaper statMaper;

    @Override
    public void saveStat(EndpointHit endpointHit) {
        Stat stat = statMaper.toStat(endpointHit);
        statRepository.save(stat);
    }

    @Override
    public Set<ViewStats> getStat(String rangeStart, String rangeEnd, String[] uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
        Set<ViewStats> result = statRepository.getStat(start, end, uris).stream()
                .map(statMaper::toViewStats)
                .collect(Collectors.toSet());
        result.forEach(viewStats -> {
            if (unique) {
                viewStats.setHits(statRepository.getUniqueHits(viewStats.getApp(), viewStats.getUri()));
            } else {
                viewStats.setHits(statRepository.countByAppAndUri(viewStats.getApp(), viewStats.getUri()));
            }
        });

        return result;
    }
}
