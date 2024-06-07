package ru.practicum.ewm.stats.service.service;


import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveEndpointHit(EndpointHitDto endpointHitDto);
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}