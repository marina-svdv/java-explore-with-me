package ru.practicum.ewm.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilation.repository.CompilationRepository;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CompilationMapper;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.CompilationNotFoundException;
import ru.practicum.ewm.main.exception.EventNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setEvents(newCompilationDto.getEvents().stream()
                .map(eventId -> eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found")))
                .collect(Collectors.toSet()));
        Compilation savedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation with id=" + compId + " was not found"));

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        compilation.setPinned(updateCompilationDto.isPinned());
        compilation.setEvents(updateCompilationDto.getEvents().stream()
                .map(eventId -> eventRepository.findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException("Event with id=" + eventId + " was not found")))
                .collect(Collectors.toSet()));

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations = (pinned == null)
                ? compilationRepository.findAll(pageRequest).getContent()
                : compilationRepository.findByPinned(pinned, pageRequest);
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }
}