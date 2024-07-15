package ru.practicum.ewm.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.practicum.ewm.main.category.repository.CategoryRepository;
import ru.practicum.ewm.main.event.dto.*;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.event.model.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.event.model.State;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.location.dto.LocationDto;
import ru.practicum.ewm.main.location.model.Location;
import ru.practicum.ewm.main.location.repository.LocationRepository;
import ru.practicum.ewm.main.notification.service.NotificationService;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.dto.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.subscription.repository.SubscriptionRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.subscription.model.Subscription;
import ru.practicum.ewm.main.user.repository.UserRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException("Event date must be at least 2 hours from the current time");
        }
        categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id=" + userId + " was not found"));
        Location location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon())
                .orElseGet(() -> locationRepository.save(newEventDto.getLocation()));

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        Event savedEvent = eventRepository.save(event);

        // уведомление подписчиков
        List<User> subscribers = subscriptionRepository.findAllByFollowing(user).stream()
                .map(Subscription::getFollower)
                .collect(Collectors.toList());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String message = String.format(
                "New event created by %s:\nTitle: %s\nDate: %s\nDescription: %s",
                user.getName(),
                event.getTitle(),
                event.getEventDate().format(formatter),
                event.getDescription()
        );
        for (User subscriber : subscribers) {
            notificationService.createNotification(subscriber, user, message);
        }


        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("User with id=" + userId + " is not the initiator of the event with id=" + eventId);
        }
        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new InvalidEventStateException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException("Event date must be at least 2 hours from the current time");
        }

        // получаем подписчиков
        List<User> subscribers = subscriptionRepository.findAllByFollowing(user).stream()
                .map(Subscription::getFollower)
                .collect(Collectors.toList());

        Event updatedEvent = EventMapper.updateEventFromUserDto(updateEventUserRequest, event);
        eventRepository.save(updatedEvent);

        // уведомление подписчиков
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String message = String.format(
                "Event updated by %s:\nTitle: %s\nDate: %s\nDescription: %s\nState: %s",
                user.getName(),
                updatedEvent.getTitle(),
                updatedEvent.getEventDate().format(formatter),
                updatedEvent.getDescription(),
                updatedEvent.getState()
        );
        for (User subscriber : subscribers) {
            notificationService.createNotification(subscriber, user, message);
        }

        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);
        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate()
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException("Event date must be at least 2 hours from the current time");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals("PUBLISH_EVENT") &&
                    !event.getState().equals(State.PENDING)) {
                throw new InvalidEventStateException("Only pending events can be published");
            }
            if (updateEventAdminRequest.getStateAction().equals("REJECT_EVENT") &&
                    !event.getState().equals(State.PENDING)) {
                throw new InvalidEventStateException("Only pending events can be rejected");
            }
        }
        if (updateEventAdminRequest.getLocationDto() != null) {
            LocationDto locationDto = updateEventAdminRequest.getLocationDto();
            Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon())
                    .orElseGet(() -> {
                        Location newLocation = new Location();
                        newLocation.setLat(locationDto.getLat());
                        newLocation.setLon(locationDto.getLon());
                        return locationRepository.save(newLocation);
                    });
            event.setLocation(location);
        }
        Event updatedEvent = EventMapper.updateEventFromAdminDto(updateEventAdminRequest, event);
        Event savedEvent = eventRepository.save(updatedEvent);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> stateStrings, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = rangeStart.plusYears(50);
        }
        log.info("Using rangeStart={} and rangeEnd={}", rangeStart, rangeEnd);

        List<State> states = null;
        if (stateStrings != null) {
            states = stateStrings.stream()
                    .map(EventMapper::stringToState)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAdminEvents(users, states, categories, rangeStart, rangeEnd, pageRequest);
        return EventMapper.toEventFullDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));

        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public EventRequestStatusUpdateResult updateEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getParticipantLimit() != 0 && !event.isRequestModeration()) {
            throw new ConflictException("Confirmation of requests is not required for this event");
        }
        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        List<Request> pendingRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
        if (pendingRequests.isEmpty()) {
            throw new ConflictException("Only pending requests can be updated");
        }

        List<Request> confirmedRequests = pendingRequests.stream()
                .filter(request -> updateRequest.getStatus() == RequestStatus.CONFIRMED)
                .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                .collect(Collectors.toList());
        List<Request> rejectedRequests = pendingRequests.stream()
                .filter(request -> updateRequest.getStatus() == RequestStatus.REJECTED)
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .collect(Collectors.toList());

        if (event.getParticipantLimit() > 0 &&
                (event.getConfirmedRequests() + confirmedRequests.size() > event.getParticipantLimit())) {
            throw new ConflictException("The participant limit has been reached");
        }

        requestRepository.saveAll(confirmedRequests);
        requestRepository.saveAll(rejectedRequests);
        event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
        eventRepository.save(event);
        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()),
                rejectedRequests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                               String sort, int from, int size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new InvalidEventDateException("rangeEnd cannot be before rangeStart");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = rangeStart.plusYears(50);
        }
        log.info("Using rangeStart={} and rangeEnd={}", rangeStart, rangeEnd);

        Pageable pageable;
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else if ("VIEWS".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            pageable = PageRequest.of(from / size, size);
        }

        Page<Event> eventPage = eventRepository.findPublicEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, pageable);
        List<Event> events = eventPage.getContent();

        // отправляем статистику для запроса
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String app = "ewm-main-service";
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            EndpointHitDto endpointHit = new EndpointHitDto(app, uri, ip, LocalDateTime.now());
            statsClient.saveEndpointHit(endpointHit);
        }

        // обновляем поле views для каждого события из списка
        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        List<ViewStats> viewStatsList = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        Map<String, Long> viewsMap = viewStatsList.stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        // получаем количество одобренных заявок для каждого события
        Map<Long, Integer> confirmedRequestsMap = events.stream()
                .collect(Collectors.toMap(Event::getId,
                        event -> requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                ));

        for (Event event : events) {
            long views = viewsMap.getOrDefault("/events/" + event.getId(), 0L);
            event.setViews(views);
            event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0));
            eventRepository.save(event);
        }
        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(Long id) {
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + id + " was not found"));

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String app = "ewm-main-service";
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            EndpointHitDto endpointHit = new EndpointHitDto(app, uri, ip, LocalDateTime.now());
            statsClient.saveEndpointHit(endpointHit);
        }

        List<ViewStats> viewStats = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of("/events/" + event.getId()), true);
        long views = viewStats.stream()
                .mapToLong(ViewStats::getHits).sum();
        event.setViews(views);

        int confirmedRequests = requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests);

        eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }
}