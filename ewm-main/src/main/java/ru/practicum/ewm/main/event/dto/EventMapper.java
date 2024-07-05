package ru.practicum.ewm.main.event.dto;

import ru.practicum.ewm.main.category.dto.CategoryMapper;
import ru.practicum.ewm.main.category.model.Category;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.State;
import ru.practicum.ewm.main.exception.InvalidEventStateException;
import ru.practicum.ewm.main.location.dto.LocationMapper;
import ru.practicum.ewm.main.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState().name(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static Event toEvent(NewEventDto newEventDto) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(new Category(newEventDto.getCategory()));
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static Event updateEventFromUserDto(UpdateEventUserRequest dto, Event event) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(new Category(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(State.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new InvalidEventStateException("Invalid state action: " + dto.getStateAction());
            }
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        return event;
    }

    public static Event updateEventFromAdminDto(UpdateEventAdminRequest dto, Event event) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(new Category(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocationDto() != null) {
            event.setLocation(LocationMapper.toLocation(dto.getLocationDto()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "REJECT_EVENT":
                    event.setState(State.CANCELED);
                    break;
                case "PUBLISH_EVENT":
                    event.setState(State.PUBLISHED);
                    break;
                default:
                    throw new InvalidEventStateException("Invalid state action: " + dto.getStateAction());
            }
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        return event;
    }

    public static State stringToState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static List<EventFullDto> toEventFullDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public static List<EventShortDto> toEventShortDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}