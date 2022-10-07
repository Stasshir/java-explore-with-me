package ru.practicum.ewm.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Set;


public interface StatRepository extends JpaRepository<Stat, Integer> {

    @Query(nativeQuery = true, value = "select  * from stat where (timestamp > ?1) " +
            "and(timestamp < ?2) and (uri in ?3)")
    Set<Stat> getStat(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(nativeQuery = true, value = "select  count(distinct ip) from stat where (app = ?1) " +
            "and(uri = ?2)")
    int getUniqueHits(String app, String uri);

    int countByAppAndUri(String app, String uri);
}
