package ru.practicum.ewm.main.event.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.event.service.EventService;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateResult;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class UserEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Creating event for user with id: {}", userId);
        log.info("Request body: {}", newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Updating event with id: {} for user with id: {}", eventId, userId);
        log.info("Request body: {}", updateEventUserRequest);
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Getting events for user with id: {}, from: {}, size: {}", userId, from, size);
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting event with id: {} for user with id: {}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }



    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting requests for event with id: {} by user with id: {}", eventId, userId);
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating requests for event with id: {} by user with id: {}", eventId, userId);
        return eventService.updateEventRequests(userId, eventId, updateRequest);
    }
}