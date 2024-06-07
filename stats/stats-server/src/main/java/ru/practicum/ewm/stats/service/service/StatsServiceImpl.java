package ru.practicum.ewm.stats.service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.service.model.EndpointHitMapper;
import ru.practicum.ewm.stats.service.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    public void saveEndpointHit(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointHitMapper.toHit(endpointHitDto));
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Getting stats from {} to {}, uris: {}, unique: {}", start, end, uris, unique);
        if (uris == null || uris.isEmpty()) {
            uris = null;
        }
        List<ViewStats> stats;
        if (unique) {
            stats = statsRepository.getUniqueStats(start, end, uris);
        } else {
            stats = statsRepository.getStats(start, end, uris);
        }
        log.info("Stats retrieved: {}", stats);
        return stats;
    }
}