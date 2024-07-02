package ru.practicum.ewm.main.compilation.dto;

import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.event.dto.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList())
        );
    }
}