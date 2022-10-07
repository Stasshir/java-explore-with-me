package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping()
@RequiredArgsConstructor
public class StatControllers {

    private final StatService statService;

    @PostMapping("/hit")
    public void saveStat(@RequestBody EndpointHit endpointHit) {
        log.info("Запрос на сохранение статистики получен");
        statService.saveStat(endpointHit);
    }

    @GetMapping("/stats")
    public Set<ViewStats> getStat(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam String[] uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на вывод статистики получен");
        return statService.getStat(start, end, uris, unique);
    }
}