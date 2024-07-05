package ru.practicum.ewm.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.request.dto.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.request.repository.RequestRepository;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found"));

        log.info("Participant limit: {}, Confirmed requests: {}", event.getParticipantLimit(), event.getConfirmedRequests());
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new RequestAlreadyExistsException("Request already exists for event with id=" + eventId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator of the event cannot create a request for their own event");
        }
        if (!event.getState().toString().equals("PUBLISHED")) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setStatus(event.getParticipantLimit() == 0 || !event.isRequestModeration() ? RequestStatus.CONFIRMED : RequestStatus.PENDING);

        Request savedRequest = requestRepository.save(request);

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Optional<Request> optionalRequest = requestRepository.findByIdAndRequesterId(requestId, userId);
        if (optionalRequest.isEmpty()) {
            throw new RequestNotFoundException("Request with id=" + requestId + " was not found");
        }
        Request request = optionalRequest.get();
        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(updatedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
}