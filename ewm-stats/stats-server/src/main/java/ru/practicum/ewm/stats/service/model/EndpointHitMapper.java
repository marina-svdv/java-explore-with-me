package ru.practicum.ewm.stats.service.model;

import ru.practicum.ewm.stats.dto.EndpointHitDto;

public class EndpointHitMapper {

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public static EndpointHit toHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp()
        );
    }
}