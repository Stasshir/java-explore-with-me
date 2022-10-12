package ru.practicum.ewm.compilation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.utils.Utils;

@Mapper(componentModel = "spring", uses = {Utils.class})
public interface CompilationMaper {
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    CompilationDto toCompilationDto(Compilation compilation);
}
