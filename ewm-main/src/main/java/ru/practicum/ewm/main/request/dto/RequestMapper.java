package ru.practicum.ewm.main.request.dto;

import ru.practicum.ewm.main.request.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getCreated(),
                request.getStatus()
        );
    }
}