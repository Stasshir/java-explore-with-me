package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryMaper;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMaper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.AdminUpdateEventRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventMaper;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidateException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMaper categoryMaper;
    private final EventMaper eventMaper;
    private final CompilationMaper compilationMaper;
    private final CompilationRepository compilationRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(Integer[] ids, int from, int size) {
        List<UserDto> result;
        result = Arrays.stream(ids).map(i -> userRepository.findById(i)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .map(UserMapper::toUserDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryMaper.toCategory(categoryDto);
        return categoryMaper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        category.setName(categoryDto.getName());
        return categoryMaper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public EventFullDto publishEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState() != State.PENDING) {
            throw new ValidateException("Событие должно быть в статусе ожидания");
        }
        if (event.getEventDate() == null) {
            throw new ValidateException("Невозможно опубликовать событие без соответствующей даты");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidateException("Дата начала события должна быть не ранее чем за час от даты публикации.");
        }
        event.setState(State.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto rejectEvent(int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState() == State.PUBLISHED) {
            throw new ValidateException("Событие не должно быть опубликовано");
        }
        event.setState(State.CANCELED);
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEvent(int eventId, AdminUpdateEventRequest eventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        event.setAnnotation(eventRequest.getAnnotation());
        event.setDescription(eventRequest.getDescription());
        event.setEventDate(eventRequest.getEventDate());
        event.setPaid(eventRequest.isPaid());
        event.setParticipantLimit(eventRequest.getParticipantLimit());
        event.setRequestModeration(eventRequest.isRequestModeration());
        event.setTitle(eventRequest.getTitle());
        return eventMaper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMaper.toCompilation(newCompilationDto);
        return compilationMaper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void addEventToCompilation(int compId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        compilation.getEvents().add(event);
        compilationRepository.save(compilation);
    }

    @Override
    public void deleteEventFromCompilation(int compId, int eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        compilation.getEvents().remove(event);
        compilationRepository.save(compilation);
    }

    @Override
    public void fixCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий не найдена"));
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public void notFixCompilation(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий не найдена"));
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilation(int compId) {
        if (!compilationRepository.existsById(compId))
            throw new NotFoundException("Подборка событий не найдена");
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<EventFullDto> findEvents(int[] users, String[] states, int[] categories,
                                         String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);
        Page<Event> events = eventRepository.findEvents(users, states, categories, start, end, pageable);
        return events.stream().map(eventMaper::toEventFullDto)
                .collect(Collectors.toList());
    }


}
