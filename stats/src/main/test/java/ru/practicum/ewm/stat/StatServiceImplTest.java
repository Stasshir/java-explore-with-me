package ru.practicum.ewm.stat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stat.dto.EndpointHit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.REQUIRED)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatServiceImplTest {

    private final StatService statService;
    @MockBean
    private final StatRepository statRepository;

    EndpointHit endpointHit1 = EndpointHit.builder().app("ewm-main-service").ip("10.0.0.000").build();
    EndpointHit endpointHit2 = EndpointHit.builder().app("ewm-stat-service").ip("10.1.1.111").build();

    Stat stat = Stat.builder().ip("10.1.1.111").app("ewm-stat-service").build();

    @Test
    void saveStat_shouldBeOk() {
        when(statRepository.save(any())).thenReturn(stat);
        statService.saveStat(endpointHit2);
        Mockito.verify(statRepository, Mockito.times(1))
                .save(stat);
    }

}