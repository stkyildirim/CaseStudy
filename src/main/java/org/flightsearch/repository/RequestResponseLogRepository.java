package org.flightsearch.repository;

import org.flightsearch.entity.RequestResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestResponseLogRepository extends JpaRepository<RequestResponseLog, Long> {

    // Provider bazlı logları getir
    List<RequestResponseLog> findByProvider(String provider);

    // Belirli tarih aralığında logları getir
    List<RequestResponseLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Provider ve tarih aralığına göre logları getir
    List<RequestResponseLog> findByProviderAndCreatedAtBetween(String provider, LocalDateTime start, LocalDateTime end);
}
