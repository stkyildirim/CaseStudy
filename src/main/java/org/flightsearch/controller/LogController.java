package org.flightsearch.controller;

import org.flightsearch.model.DateRangeRequest;
import org.flightsearch.model.FilterRequest;
import org.flightsearch.model.LogResponse;
import org.flightsearch.model.RequestResponseLogDTO;
import org.flightsearch.service.LogService;
import org.flightsearch.validation.LogRequestValidator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;


    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping("/provider")
    @Cacheable(value = "logsByProvider", key = "#providers.stream().sorted().collect(T(java.util.stream.Collectors).joining(','))")
    public LogResponse getLogsByProvider(@RequestBody List<String> providers) {
        // Validate
        LogResponse response = new LogResponse();
        LogRequestValidator.validateProviderOnly(providers,response);
        if (response.isHasError())
            return  response;
        List<RequestResponseLogDTO> result = new ArrayList<>();
        for (String provider : providers) {
            result.addAll(logService.getLogsByProvider(provider));
        }
        response.setLogs(result);
        return response;
    }

    @PostMapping("/date")
    public LogResponse getLogsByDateRange(@RequestBody DateRangeRequest request) {
        // Validate
        LogResponse response = new LogResponse();
        LogRequestValidator.validateDatesOnly(request.getStart(), request.getEnd(),response);
        if (response.isHasError())
            return  response;

        LocalDateTime startDate = LocalDateTime.parse(request.getStart(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getEnd(), formatter);
        response.setLogs(logService.getLogsByDateRange(startDate, endDate));
        return response;
    }

    @PostMapping("/filter")
    public LogResponse getLogsByProviderAndDateRange(@RequestBody FilterRequest request) {
        // Validate
        LogResponse response = new LogResponse();
        LogRequestValidator.validateProviderAndDates(request.getProviders(), request.getStart(), request.getEnd(),response);
        if (response.isHasError())
            return  response;
        LocalDateTime startDate = LocalDateTime.parse(request.getStart(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(request.getEnd(), formatter);

        List<RequestResponseLogDTO> result = new ArrayList<>();
        for (String provider : request.getProviders()) {
            result.addAll(logService.getLogsByProviderAndDateRange(provider, startDate, endDate));
        }
        response.setLogs(result);
        return response;
    }
}
