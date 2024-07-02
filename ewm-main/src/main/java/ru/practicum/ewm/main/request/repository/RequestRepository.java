package ru.practicum.ewm.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndRequesterId(Long requestId, Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByEventId(Long eventId);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = :status")
    int countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RequestStatus status);
}