package ru.practicum.ewm.main.compilation.service;

import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    void deleteCompilation(Long compId);
}