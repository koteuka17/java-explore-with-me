package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.model.ViewStatsShort;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(" SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.created BETWEEN ?1 AND ?2 " +
            "AND (eh.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(DISTINCT eh.ip) DESC " +
            "LIMIT ?4")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Integer limit);

    @Query(" SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.created BETWEEN ?1 AND ?2 " +
            "AND (eh.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY COUNT(eh.ip) DESC " +
            "LIMIT ?4")
    List<ViewStats> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Integer limit);

    @Query(" SELECT new ru.practicum.model.ViewStatsShort(eh.ip, eh.uri) " +
            "FROM EndpointHit eh " +
            "WHERE eh.ip = (?1) AND eh.uri = (?2)")
    List<ViewStatsShort> findUniqueIp(String ip, String uri);
}