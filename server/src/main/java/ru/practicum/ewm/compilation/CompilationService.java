package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;


public interface CompilationService {

    CompilationDto getCompilationsById(int compId);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);
}
