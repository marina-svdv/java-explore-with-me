package ru.practicum.ewm.main.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(Long categoryId);

    List<Event> findByInitiatorId(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd)")
    List<Event> findAdminEvents(@Param("users") List<Long> users,
                                @Param("states") List<State> states,
                                @Param("categories") List<Long> categories,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id = ?1 AND e.state = ?2")
    Optional<Event> findByIdAndState(Long id, State state);

    @Query("SELECT e FROM Event e WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.participantLimit = 0 " +
            "OR e.confirmedRequests < e.participantLimit)")
    Page<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 @Param("onlyAvailable") boolean onlyAvailable,
                                 Pageable pageable);
}