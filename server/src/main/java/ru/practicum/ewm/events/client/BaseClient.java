package ru.practicum.ewm.events.client;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<List<ViewStats>> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<List<ViewStats>> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<List<ViewStats>> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> ResponseEntity<List<ViewStats>> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        var type = new ParameterizedTypeReference<List<ViewStats>>() {
        };

        ResponseEntity<List<ViewStats>> ewmStatResponse;
        try {
            if (parameters != null) {
                ewmStatResponse = rest.exchange(path, method, requestEntity, type, parameters);
            } else {
                ewmStatResponse = rest.exchange(path, method, requestEntity, type);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
            //e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(ewmStatResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<List<ViewStats>> prepareGatewayResponse(ResponseEntity<List<ViewStats>> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
