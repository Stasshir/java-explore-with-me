package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public void saveStat(@RequestBody EndpointHit endpointHit) {
        log.info("Запрос на сохранение статистики получен по эндпоинту, URI={}", endpointHit.getUri());
        statService.saveStat(endpointHit);
    }

    @GetMapping("/stats")
    public Set<ViewStats> getStat(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam @NotNull String[] uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на вывод статистики по эндпоинтам получен, URI={}", uris);
        return statService.getStat(start, end, uris, unique);
    }
}