package ru.practicum.ewm.main.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.location.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(double lat, double lon);
}