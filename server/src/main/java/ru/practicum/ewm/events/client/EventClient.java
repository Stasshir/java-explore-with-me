package ru.practicum.ewm.events.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class EventClient extends BaseClient {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EventClient(@Value("${explore-with-me-stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void postStat(String app, String uri, String ip) {
        Map<String, Object> parameters = Map.of(
                "app", app,
                "uri", uri,
                "ip", ip,
                "timestamp", LocalDateTime.now().format(formatter)
        );
        post("/hit", parameters);
    }


    public ResponseEntity<List<ViewStats>> getStats(LocalDateTime start, LocalDateTime end, String urii) {
        String starts = start.format(formatter);
        String ends = end.format(formatter);
        String[] uri = new String[1];
        uri[0] = urii;
        Map<String, Object> parameters = Map.of(
                "start", starts,
                "end", ends,
                "uris", uri
        );

        return get("/stats?start={start}&end={end}&uris={uris}", 0L, parameters);
    }
}
