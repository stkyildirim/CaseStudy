package org.flightsearch.service;

import org.flightsearch.entity.RequestResponseLog;
import org.flightsearch.model.RequestResponseLogDTO;
import org.flightsearch.repository.RequestResponseLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    private final RequestResponseLogRepository logRepository;

    @Autowired
    public LogService(RequestResponseLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    private RequestResponseLogDTO mapToDTO(RequestResponseLog log) {
        return new RequestResponseLogDTO(
                log.getId(),
                log.getProvider(),
                log.getRequest(),
                log.getResponse(),
                log.getCreatedAt()
        );
    }

    public List<RequestResponseLogDTO> getLogsByProvider(String provider) {
        return logRepository.findByProvider(provider)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RequestResponseLogDTO> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return logRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RequestResponseLogDTO> getLogsByProviderAndDateRange(String provider, LocalDateTime start, LocalDateTime end) {
        return logRepository.findByProviderAndCreatedAtBetween(provider, start, end)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
