package org.flightsearch.controller;

import org.flightsearch.model.SearchRequest;
import org.flightsearch.model.SearchResponse;
import org.flightsearch.service.FlightSearchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flights")
public class FlightSearchController {

    private final FlightSearchService flightSearchService;

    public FlightSearchController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    /**
     * 2.1: Tüm uçuşları getirir
     */
    @PostMapping("/all")
    public SearchResponse getAllFlights(@RequestBody SearchRequest request) {
        return flightSearchService.getAllFlights(request);
    }

    /**
     * 2.2: En ucuz uçuşları gruplayarak getirir
     */
    @PostMapping("/cheapest")
    public SearchResponse getCheapestGroupedFlights(@RequestBody SearchRequest request) {
        return flightSearchService.getCheapestGroupedFlights(request);
    }
}

