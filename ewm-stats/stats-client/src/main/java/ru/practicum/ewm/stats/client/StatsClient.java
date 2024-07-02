package ru.practicum.ewm.stats.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public StatsClient(RestTemplate restTemplate, @Value("${stats.server.url}") String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public void saveEndpointHit(EndpointHitDto endpointHitDto) {
        log.info("Sending endpoint hit: {}", endpointHitDto);
        restTemplate.postForEntity(serverUrl + "/hit", endpointHitDto, Void.class);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("unique", unique);
        if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
        }
        URI uri = builder.build().toUri();
        log.info("Getting stats with URI: {}", uri);
        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                uri,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {}
        );
        return response.getBody();
    }
}