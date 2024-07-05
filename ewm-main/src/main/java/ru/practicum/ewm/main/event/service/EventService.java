package ru.practicum.ewm.main.event.service;

import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getEventsByUser(Long userId, int from, int size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                        String sort, int from, int size);

    EventFullDto getPublicEventById(Long id);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequests(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}